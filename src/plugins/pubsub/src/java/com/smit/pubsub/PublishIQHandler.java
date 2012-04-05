package com.smit.pubsub;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.smit.database.DatabaseMan;
import com.smit.vo.AtomRecord;
import com.smit.vo.RssRecord;
import com.smit.vo.Subscriber;

public class PublishIQHandler extends IQHandler {

	private static final String NAME_SPACE = "smit:pubsub:publish";
	private static final String MODULE_NAME = "PubsubPublishHandler";
	private IQHandlerInfo info;
	private XMPPServer server;
	private SessionManager sessionManager;

	public PublishIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo("query", NAME_SPACE);
		server = XMPPServer.getInstance();
		sessionManager = server.getSessionManager();
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		System.out.println("feed update!");
		IQ reply = null;
		String nameSpace = packet.getChildElement().getNamespaceURI();
		if (nameSpace.equals(NAME_SPACE)) {
			if (packet.getType() == IQ.Type.set) {
				processSetPacket(packet);
			}
		}
		return null;
	}

	private void processSetPacket(IQ packet) {
		IQ reply = IQ.createResultIQ(packet);
		Element root = packet.getChildElement();
		try {
			String feed = root.elementText("feed");
			// 取feed地址的内容
			HttpClient client = new HttpClient();
			GetMethod getmethod = new GetMethod(feed);
			client.executeMethod(getmethod);
			InputStream in = getmethod.getResponseBodyAsStream();
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(in);
			Map<String, String> nameSpaceMap = new HashMap<String, String>();
		    nameSpaceMap.put("atom", "http://www.w3.org/2005/Atom");   
		    saxReader.getDocumentFactory().setXPathNamespaceURIs(nameSpaceMap); 
			String url = getFeedUrl(doc);
			System.out.println(url);
			if(url==null){
				url = feed;
			}
			parseXML(doc, url);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

	private String getFeedUrl(Document doc) {
		String expression = null;
		String url = null;
		try{
			expression = "//link[@rel='self']";
			List linklist = doc.selectNodes(expression);
			if (linklist.size() == 0) {
				expression = "//atom:link[@rel='self']";
				linklist = doc.selectNodes(expression);
			}
			Element linkele = (Element) linklist.get(0);
			url = linkele.attributeValue("href");
		}catch (Exception e){
			e.printStackTrace();
		}
		return url;
	}

	private void parseXML(Document doc, String url) throws Exception {
		String expression;
		String result = "";
		// atom
		expression = "/feed";
		List listfeed = doc.selectNodes(expression);
		int entryNum = 0;
		if (listfeed.size() > 0) {
			// 去除重复的
			List listEntry = doc.selectNodes("/feed//atom:entry");
			List<AtomRecord> alist = (List<AtomRecord>) DatabaseMan.select("from AtomRecord a where a.feed_url='"+url+"'");
			List<AtomRecord> lists = new ArrayList<AtomRecord>();
			for (Iterator i = listEntry.iterator(); i.hasNext();) {
				Element entry = (Element) i.next();
				int count = 0;
				for (AtomRecord r : alist) {
					if (r.getAtom_id().equals(entry.elementText("id"))) {
						entry.detach();
						entryNum++;
						count++;
					}
				}
				System.out.println("feed count is:" + count);
				if (count <= 0) {
					AtomRecord record = new AtomRecord();
					record.setTitle(entry.elementText("title"));
					record.setLink(entry.element("link").attributeValue("href"));
					record.setAtom_id(entry.elementText("id"));
					record.setUpdate_time(entry.elementText("updated"));
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyyMMdd HH:mm:ss");
					record.setCreate_time(sdf.format(new Date()));
					record.setFeed_url(url);
					lists.add(record);
				}
			}
			DatabaseMan.addListAtom(lists);
			result = doc.asXML();
			// 有了更新才发通知
			if (entryNum != listEntry.size()) {
				postNotifytoSub(doc, url);
			}
			return;
		}
		// rss
		expression = "/rss";
		List listrss = doc.selectNodes(expression);
		if (listrss.size() > 0) {
			// 去除重复的
			List listItem = doc.selectNodes("/rss/channel/item");
			List<RssRecord> rlist = (List<RssRecord>) DatabaseMan.select("from RssRecord r where r.feed_url='"+url+"'");
			List<RssRecord> lists = new ArrayList<RssRecord>();
			for (Iterator i = listItem.iterator(); i.hasNext();) {
				Element item = (Element) i.next();
				int count = 0;
				for (RssRecord r : rlist) {
					if (r.getGuid().equals(item.elementText("guid"))) {
						item.detach();
						entryNum++;
						count++;
					}
				}
				System.out.println(count);
				if (count <= 0) {
					RssRecord record = new RssRecord();
					record.setTitle(item.elementText("title"));
					record.setLink(item.elementText("link"));
					record.setPubDate(item.elementText("pubDate"));
					record.setGuid(item.elementText("guid"));
					record.setFeed_url(url);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
					record.setCreate_time(sdf.format(new Date()));
					lists.add(record);
				}
			}
			DatabaseMan.addListRss(lists);
			result = doc.asXML();
			// 有更新才发通知给sub
			System.out.println(entryNum);
			if (entryNum != listItem.size()) {
				 postNotifytoSub(doc, url);
			}
		}
	}

	private void postNotifytoSub(Document doc, String url) {
		List<Subscriber> subs = (List<Subscriber>) DatabaseMan.select("from Subscriber s where s.feed='"+url+"'");
		Element root = doc.getRootElement();
		System.out.println(root.getName());
		Message message = new Message();
		message.setFrom("server@smit/Smack");
		Element event = message.addChildElement("event", "smit:pubsub:notification");
		
		if(root.getName().equals("rss")){
			//rss
			event.addElement("title").addText(root.element("channel").elementText("title"));
			Element items = event.addElement("items");
			items.addAttribute("node", url);
			List<Element> itemlist = root.element("channel").elements("item");
			for(Element e:itemlist){
				Element item = items.addElement("item");
				item.addElement("title").setText(e.elementText("title"));
				item.addElement("summary").setText(e.elementText("description"));
				item.addElement("link").addAttribute("href", e.elementText("link"));
				item.addElement("id").setText(e.elementText("guid"));
				item.addElement("published").setText(e.elementText("pubDate"));
			}
		}else{
			//atom
			event.addElement("title").addText(root.elementText("title"));
			Element items = event.addElement("items");
			items.addAttribute("node", url);
			List<Element> entrylist = root.elements("entry");
			for(Element e:entrylist){
				Element item = items.addElement("item");
				item.addElement("title").setText(e.elementText("title"));
				item.addElement("summary").setText(e.elementText("summary"));
				item.addElement("link").addAttribute("href", e.element("link").attributeValue("href"));
				item.addElement("id").setText(e.elementText("id"));
				item.addElement("published").setText(e.elementText("updated"));
			}
		}
		System.out.println(message.toXML());
		for(Subscriber s: subs){
			ClientSession session = sessionManager.getSession(new JID(s.getJid()));
			if(session!=null){
				session.process(message);
				System.out.println("message send success!");
			}
		}
	}
			
}
