package com.hand.hrms4android.parser.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;
import com.hand.hrms4android.util.StorageUtil;

public class XmlConfigReader implements ConfigReader {

	/**
	 * xml文件来源
	 */
	private InputSource xmlInputSource;

	/**
	 * xpath解析器
	 */
	private XPath xpath;

	public XmlConfigReader() throws FileNotFoundException {
		File xmlFile = StorageUtil.getFile("android-backend-config.xml");
		xmlInputSource = new InputSource(new FileInputStream(xmlFile));
		xpath = XPathFactory.newInstance().newXPath();
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
