package com.hdf.easytools.utils;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxXmlEntiy extends DefaultHandler {
	private StringBuffer buf;
	private String str;

	public SaxXmlEntiy() {
		super();
	}

	public void startDocument() throws SAXException {
		buf = new StringBuffer();
		System.out.println("*******开始解析XML*******");
	}

	public void endDocument() throws SAXException {
		System.out.println("*******XML解析结束*******");
	}

	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		str = buf.toString();
		System.out.println("节点=" + fullName + "\tvalue=" + buf + " 长度=" + buf.length());
		System.out.println();
		buf.delete(0, buf.length());
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		// 将元素内容累加到StringBuffer中
		buf.append(chars, start, length);
	}
}
