package com.hand.hrms4android.util;

import java.io.File;

import android.os.Environment;

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
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "hand/"
				+ relativeFilePath);
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
}
