package com.hdf.easytools.utils;

import java.io.StringReader;
//1
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//end
//2
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//end

public class XmlResolveUtils {
	/**
	 * java自带的DOM解析.
	 * 
	 * @param protocolXML
	 */
	public static void jDomXml(String protocolXML) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(protocolXML)));

			Element root = doc.getDocumentElement();
			NodeList books = root.getChildNodes();
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					System.out.println("节点=" + book.getNodeName() + "\ttext=" + book.getFirstChild().getNodeValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saxXml(String protocolXML) {

		try {
			SAXParserFactory saxfac = SAXParserFactory.newInstance();
			SAXParser saxparser = saxfac.newSAXParser();
			SaxXmlEntiy tsax = new SaxXmlEntiy();
			saxparser.parse(new InputSource(new StringReader(protocolXML)), tsax);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
