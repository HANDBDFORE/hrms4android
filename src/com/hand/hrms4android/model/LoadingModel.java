package com.hand.hrms4android.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.hand.hrms4android.activity.ModelActivity;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.exception.ParseException;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class LoadingModel extends AbstractBaseModel<Void> {

	public LoadingModel(int id, ModelActivity activity) {
		super(id, activity);
	}

	@Override
	public void load(LoadType loadType, Object param) {
		String baseUrl = (String) param;
		String url = baseUrl + "/android-backend-config-aries.xml";

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, null, new BinaryHttpResponseHandler(new String[] { "application/xml" }) {

			@Override
			public void onSuccess(int statusCode, byte[] binaryData) {
				super.onSuccess(statusCode, binaryData);

				File dir = HrmsApplication.getApplication().getDir(Constrants.SYS_PREFRENCES_CONFIG_FILE_DIR_NAME,
				        Context.MODE_PRIVATE);
				File configFile = new File(dir, "android-backend-config.xml");
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(configFile);
					fileOutputStream.write(binaryData);
				} catch (Exception ex) {
					// 捕获错误
					activity.modelFailedLoad(ex, LoadingModel.this);
					return;
				}finally{
					try {
	                    fileOutputStream.close();
                    } catch (IOException e) {
	                    e.printStackTrace();
                    }
				}

				// 检查配置文件是否正确
				try {
					ConfigReader reader = XmlConfigReader.getInstance();
					reader.getAttr(new Expression("/config", ""));
				} catch (Exception e) {
					activity.modelFailedLoad(new ParseException(e.getMessage()), LoadingModel.this);
					return;
				}

				activity.modelDidFinishedLoad(LoadingModel.this);
			}

			@Override
			public void onFailure(Throwable error, byte[] binaryData) {
				activity.modelFailedLoad(new IOException(error.getMessage()), LoadingModel.this);
			}
		});
	}
}
