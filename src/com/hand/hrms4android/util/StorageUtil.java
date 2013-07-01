package com.hand.hrms4android.util;

import java.io.File;

import com.hand.hrms4android.application.HrmsApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;

public class StorageUtil {

	public static File getFile(String relativeFilePath) {
		File file = null;
		if (isSDCardMounted()) {
			file = getFileInSDCard(relativeFilePath);
		} else {
			file = getFileInDataDirectory(relativeFilePath);
		}
		return file;
	}

	public static File getFileInSDCard(String relativeFilePath) {
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "hand/" + relativeFilePath);
		Environment.getDataDirectory();
		return file;
	}

	/**
	 * @param relativeFilePath
	 * @return
	 */
	public static File getFileInDataDirectory(String relativeFilePath) {
		File file = new File(Environment.getDataDirectory(), relativeFilePath);
		return file;
	}

	public static boolean isSDCardMounted() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除所有数据库文件
	 */
	public static void deleteDB() {
		File f = new File("/data/data/" + HrmsApplication.getApplication().getPackageName() + "/databases");

		if (f.exists()) {
			String[] fileNames = f.list();
			for (String fileName : fileNames) {
				File dbFile = HrmsApplication.getApplication().getDatabasePath(fileName);
				dbFile.delete();
			}
		}
	}

	/**
	 * 删除存储在preference中的session信息
	 */
	public static void removeSavedInfo() {
		SharedPreferences savedPreferences = PreferenceManager.getDefaultSharedPreferences(HrmsApplication
		        .getApplication());
		Editor editor = savedPreferences.edit();
		editor.remove(Constrants.SYS_PREFRENCES_TOKEN);
		editor.remove(Constrants.SYS_PREFRENCES_ENCRYTED_SESSION_ID);
		editor.remove(Constrants.SYS_PREFRENCES_PUSH_TOKEN);
		editor.remove(Constrants.SYS_PREFRENCES_USERNAME);
		editor.commit();
	}

	/**
	 * 删除存储的配置文件
	 */
	public static void removeCachedConfigFile() {
		File dir = HrmsApplication.getApplication().getDir(Constrants.SYS_PREFRENCES_CONFIG_FILE_DIR_NAME,
		        Context.MODE_PRIVATE);
		File configFile = new File(dir, "android-backend-config.xml");
		configFile.delete();
	}
}
