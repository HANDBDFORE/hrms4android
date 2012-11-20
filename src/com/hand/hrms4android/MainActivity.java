package com.hand.hrms4android;

import java.io.FileNotFoundException;

import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.exception.ParseExpressionException;
import com.hand.hrms4android.parser.xml.XmlConfigReader;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ConfigReader reader;
		try {
			reader = new XmlConfigReader();
			String s = reader.getAttr(new Expression("//activity[@name='activityB']/titleText", "value"));
			System.out.println("==============" + s);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ParseExpressionException e) {
			e.printStackTrace();
		}
	}

}
