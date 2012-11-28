package com.hand.hrms4android.parser.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.hand.hrms4android.application.HrmsApplication;
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

	public XmlConfigReader(File xmlFile) throws FileNotFoundException {
		xmlInputSource = new InputSource(new FileInputStream(xmlFile));
	}

	public XmlConfigReader(InputStream inputStream) throws FileNotFoundException {
		xmlInputSource = new InputSource(inputStream);
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
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputStream configFileInputStream = null;
		try {
			configFileInputStream = HrmsApplication.getApplication().getAssets()
			        .open("android-backend-config.xml");
			xmlInputSource = new InputSource(configFileInputStream);
			Object result = xpath.evaluate(expression, xmlInputSource, XPathConstants.NODE);
			if (result != null && result instanceof Element) {
				return (Element) result;
			} else {
				throw new ParseExpressionException(
				        "Can't find the node or the result is not an instance of Element ,expression:"
				                + expression);
			}
		} catch (XPathExpressionException e) {
			throw new ParseExpressionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseExpressionException(e);
		} finally {
			if (configFileInputStream != null) {
				try {
					configFileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
