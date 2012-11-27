package com.hand.hrms4android.parser.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;

public class XmlConfigReader implements ConfigReader {

	/**
	 * xml文件来源
	 */
	private InputSource xmlInputSource;

	private static XmlConfigReader configReader;

	/**
	 * xpath解析器
	 */
	private XPath xpath;

	public XmlConfigReader(File xmlFile) throws FileNotFoundException {
		xmlInputSource = new InputSource(new FileInputStream(xmlFile));
		xpath = XPathFactory.newInstance().newXPath();
	}

	public XmlConfigReader(InputStream inputStream) throws FileNotFoundException {
		xmlInputSource = new InputSource(inputStream);
		xpath = XPathFactory.newInstance().newXPath();
	}

	public static XmlConfigReader createInstanceByInputStream(InputStream inputStream) {
		if (configReader == null) {
			try {
				configReader = new XmlConfigReader(inputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return configReader;
	}

	public static XmlConfigReader getInstance() {
		if (configReader == null) {
			throw new RuntimeException(
					"must call createInstanceByInputStream before use this class");
		}
		return configReader;
	}

	@Override
	public String getAttr(Expression expression) throws ParseExpressionException {
		Element element = getElement(expression.getExpression());
		return element.getAttribute(expression.getAttName());
	}

	public Element getElement(String expression) throws ParseExpressionException {
		try {
			Object result = xpath.evaluate(expression, xmlInputSource, XPathConstants.NODE);
			if (result != null && result instanceof Element) {
				return (Element) result;
			} else {
				throw new ParseExpressionException(
						"Can't find the node or the result is not an instance of Element ");
			}
		} catch (XPathExpressionException e) {
			throw new ParseExpressionException(e);
		}
	}

}
