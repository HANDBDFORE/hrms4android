package com.hand.hrms4android.model;

public class Employee {
	private String employeeName;
	private String employeeNo;
	private String employeeTel;
	private String employeeJob;
	
	public Employee (String employeeName, String employeeNo, String employeeTel, String employeeJob){
		this.employeeName = employeeName;
		this.employeeNo = employeeNo;
		this.employeeTel = employeeTel;
		this.employeeJob = employeeJob;
		
	}
	
	public String getName() {
		return employeeName;
	}
	
	public String getNo() {
		return employeeNo;
	}
	
	public String getTel() {
		return employeeTel;
	}
	
	public String getJob() {
		return employeeJob;
	}
}
