package com.hand.hrms4android.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.core.HDAbstractModel;
import com.hand.hrms4android.core.ModelViewController;
import com.hand.hrms4android.network.HDStringRequest;
import com.hand.hrms4android.parser.ConfigReader;
import com.hand.hrms4android.parser.Expression;
import com.hand.hrms4android.parser.xml.XmlConfigReader;
import com.hand.hrms4android.util.Constrants;

public class LoadingModel extends HDAbstractModel {

	public LoadingModel(int id, ModelViewController controller) {
		super(id, controller);
	}

	@Override
	public void load(LoadType loadType, Object param) {
		String baseUrl = param.toString();
		String url = baseUrl + "/android-backend-config-aries.xml";
		requestQueue.add(genRequest(this, url));
	}

	private Request<String> genRequest(Object tag, String url) {
		Response.Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				File dir = HrmsApplication.getApplication().getDir(Constrants.SYS_PREFRENCES_CONFIG_FILE_DIR_NAME,
				        Context.MODE_PRIVATE);
				File configFile = new File(dir, "android-backend-config.xml");
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(configFile);
					fileOutputStream.write(response.getBytes("UTF-8"));
				} catch (Exception ex) {
					// 捕获错误
					loadFailed(ex);
					return;
				} finally {
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
					loadFailed(e);
					return;
				}

				onLoadingEnd();
				controller.modelDidFinishedLoad(LoadingModel.this);
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onLoadingEnd();
				loadFailed(new IOException(error.getMessage()));
			}
		};

		HDStringRequest srequest = new HDStringRequest(controller.getContext(), Method.GET, url, listener,
		        errorListener);

		srequest.setTag(tag);

		return srequest;
	}

	/**
	 * @param ex
	 */
	private void loadFailed(Exception ex) {
		controller.modelFailedLoad(ex, LoadingModel.this);
	}

	@Override
	public <T> T getProcessData() {
		throw new UnsupportedOperationException("不支持");
	}
}
