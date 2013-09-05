package com.hand.hrms4android.network;

import java.util.Map;

public interface RequestParameterSource {

	public Map<String, String> getParams();

	public String getParamsAsString();

}
