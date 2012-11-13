package com.hand.hrms4android.network.util;

import com.hand.hrms4android.network.NetworkException;


public class ResponseCheckUtils {
	public static final String ERROR_CODE_PARSE_JSON="50";
	public static final String ERROR_CODE_NULL_RESPONSE = "52";

	public static void parseErrorException(String method, String body)throws NetworkException{
			throw new NetworkException(ERROR_CODE_PARSE_JSON,"server-error:"+method+":body:"+body+"");
	}
	
	public static void responseErrorException(String method)throws NetworkException{
		throw new NetworkException(ERROR_CODE_NULL_RESPONSE,"server-error:"+method);
}
}
