package com.hand.hrms4android.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hand.hrms4android.R;
import com.hand.hrms4android.application.HrmsApplication;
import com.hand.hrms4android.listable.adapter.DeliverListAdapter;
import com.hand.hrms4android.model.DeliverModel;
import com.hand.hrms4android.model.Employee;
import com.hand.hrms4android.model.Model;
import com.hand.hrms4android.model.Model.LoadType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class EmployeeListActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private SearchView sv;
    private ListView lv;
//    private final ArrayList<String> mStrings=new ArrayList<String>();
//    private ArrayAdapter<String> nameList;
    private ArrayList<Employee> employeeList = new ArrayList<Employee>();
    private DeliverListAdapter adapter;
    private String sourceSystemName;
    private List<Map<String, String>> rawData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		HrmsApplication.getApplication().addActivity(this);
        
        setContentView(R.layout.activity_employee_search);
        model = new DeliverModel(0, this);
        
        lv=(ListView)findViewById(R.id.lv);
        lv.setTextFilterEnabled(false);   
        adapter = new DeliverListAdapter(this,R.layout.activity_deliver_item,employeeList);
        lv.setAdapter(adapter);        
        sv=(SearchView)findViewById(R.id.employeeName);
        //设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(false);
        //为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        //设置该SearchView显示搜索按钮
        sv.setSubmitButtonEnabled(true);
        //设置该SearchView内默认显示的提示文本
        sv.setQueryHint(getResources().getString(R.string.activity_employee_list_no_or_name));        
        
        //绑定listview事件
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自动生成的方法存根
				Intent infoIntent = new Intent();
				infoIntent.putExtra("employeeName", rawData.get(position).get("name"));
				infoIntent.putExtra("employeeId", rawData.get(position).get("employee_id"));
				EmployeeListActivity.this.setResult(RESULT_OK, infoIntent);
				EmployeeListActivity.this.finish();
			}
        	
		}); 
        sourceSystemName = getIntent().getStringExtra("sourceSystemName");
    }

    @Override
    public void modelDidFinishedLoad(Model model) {
    	rawData =  (List<Map<String, String>>) model.getProcessData();
    	for(int i= 0;i<rawData.size();i++){
    		Employee item = new Employee(rawData.get(i).get("name"), rawData.get(i).get("employee_code"), rawData.get(i).get("mobile"),rawData.get(i).get("job"));
    		employeeList.add(item);
    	}
        adapter = new DeliverListAdapter(this,R.layout.activity_deliver_item,employeeList);
        lv.setAdapter(adapter);
    }
    
	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO 自动生成的方法存根
		employeeList.clear();
		if(!query.toString().isEmpty())
			model.load(LoadType.Network, new String[]{sourceSystemName,query.toString()});
//		Toast.makeText(this, "您选择的是："+query, Toast.LENGTH_SHORT).show();
		return false;
	}

    //用户输入字符时激发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        if(TextUtils.isEmpty(newText))
        {
            //清楚ListView的过滤
            lv.clearTextFilter();
        }
        else
        {
            //使用用户输入的内容对ListView的列表项进行过滤
//            lv.setFilterText(newText);
        
        }
        return true;
    }	
    

}