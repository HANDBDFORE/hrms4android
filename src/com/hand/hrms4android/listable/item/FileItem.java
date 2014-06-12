package com.hand.hrms4android.listable.item;

import java.io.File;

public class FileItem {
	private File file;
	private String end;
	private String fileName;

	public FileItem(File file) {
		this.file = file;
		this.fileName = file.getName();
		this.end = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();
	}

	public File getFile() {
		return this.file;
	}

	public String getTitle() {
		return this.fileName;
	}

	public String getEnd() {
		return end;
	}

	public String getSize() {
		float fl = file.length() / 1024;

		String temp = String.format("%.1f", fl) + "K";
		return temp;

	}
}