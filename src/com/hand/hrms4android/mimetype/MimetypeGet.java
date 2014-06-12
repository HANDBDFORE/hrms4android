package com.hand.hrms4android.mimetype;

public class MimetypeGet {
	public static String getByFileName(String FileName) {

		String end = FileName.substring(FileName.lastIndexOf(".") + 1,
				FileName.length()).toLowerCase();
		String mimetype;
		if (end.equals("jpg") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			mimetype = "image/jpeg";

		} else if (end.equals("doc") || end.equals("docx")) {
			mimetype = "application/msword";

		} else if (end.equals("pdf")) {

			mimetype = "application/pdf";
     
		}else if(end.equals("xls") || end.equals("xlsx")){
			
			mimetype = "application/vnd.ms-excel";
		}else if(end.equals("pptx") || end.equals("ppt")){
			mimetype = "application/vnd.ms-powerpoint";
		}else if(end.equals("txt")){
			mimetype = "text/plain";
		
		}else {
			mimetype = "*/*";
		}

		return mimetype;
	}

}
