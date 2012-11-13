package com.hand.hrms4android.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.hand.hrms4android.network.util.HDHashMap;

public class HDHeader {
	private String imei="";
	private String mob="";
	private String os="";
	private String dev="";
	private String ver="";
	private String chnl="";
	private Long t;
	private String sign;
	private String gender="0";
	private int age=0;
	private long did = 0;
	
	private static final String IMEI = "imei";
	private static final String MOBILE = "mob";
	private static final String OS = "os";
	private static final String DEVICE = "dev";
	private static final String VERSION = "ver";
	public static final String CHANNEL = "chnl";
	public static final String TIMESTAMP = "t";
	public static final String SIGN = "sign";
	private static final String GENDER = "gender";
	private static final String AGE = "age";
	private static final String DEVICEID = "did";
	public  static final String METHOD = "m";
	
	private static HDHeader _instance = null;
	
	private HDHeader()
	{
		ver = "1.0.0";
	}
	
	public static HDHeader getInstance()
	{
		if (null == _instance)
		{
			_instance = new HDHeader();
		}
		return _instance;
	}
	
	public HDHashMap getCurrentHead()
	{
		HDHashMap ret = new HDHashMap();
		ret.put(IMEI, imei);
		ret.put(MOBILE, mob);
		ret.put(OS, os);
		ret.put(DEVICE, dev);
		ret.put(VERSION, ver);
//		ret.put(CHANNEL, chnl);
//		ret.put(TIMESTAMP, t);
		
		ret.put(GENDER, gender);
		ret.put(AGE, age);
		ret.put(DEVICEID, did);
		return ret;
	}
	
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getMob() {
		return mob;
	}
	public void setMob(String mob) {
		this.mob = mob;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getDev() {
		return dev;
	}
	public void setDev(String dev) {
		this.dev = dev;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getChnl() {
		return chnl;
	}
	public void setChnl(String chnl) {
		this.chnl = chnl;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Long getT() {
		if (t==null) {
			t=new Date().getTime();
		}
		return t;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public List<Header> createHeaderList(){
		List<Header> headers=new ArrayList<Header>();
		headers.add(new BasicHeader("mei", imei));
		headers.add(new BasicHeader("mob", mob));
		headers.add(new BasicHeader("os", os));
		headers.add(new BasicHeader("dev", dev));
		headers.add(new BasicHeader("ver", ver));
		headers.add(new BasicHeader("chnl", chnl));
		headers.add(new BasicHeader("t", String.valueOf(getT())));
		headers.add(new BasicHeader("sign", sign));
		headers.add(new BasicHeader("gender", gender));
		headers.add(new BasicHeader("age", String.valueOf(age)));
		return headers;
	}
}