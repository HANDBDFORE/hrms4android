package com.hand.hrms4android.listable.adapter;

import java.util.List;

import com.hand.hrms4android.R;
import com.hand.hrms4android.model.Employee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeliverListAdapter extends ArrayAdapter<Employee>{

	private int resourceId;
	
	public DeliverListAdapter(Context context, int resource,
			List<Employee> objects) {
		super(context, resource, objects);
		resourceId = resource;
		// TODO 自动生成的构造函数存根
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Employee employee = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView employeeName = (TextView) view.findViewById(R.id.employeeName);
        TextView employeeNo = (TextView) view.findViewById(R.id.employeeNo);
        TextView employeeTel = (TextView) view.findViewById(R.id.employeeTel);
        TextView employeeEmail = (TextView) view.findViewById(R.id.employeeJob);
        employeeName.setText(employee.getName());
        employeeNo.setText("No:".concat(employee.getNo()));
        employeeTel.setText(employee.getTel());   
        employeeEmail.setText("Job:".concat(employee.getJob()));
        return view;
		
    }

}
