package com.hand.hrms4android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.adapter.DownLoadListAdapter;
import com.hand.hrms4android.listable.item.FileItem;
import com.hand.hrms4android.model.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class DownLoadListFragment extends BaseSherlockFragment implements
		OnItemLongClickListener {

	public static final String DOWNLOAD_DIR = "HandMobile";
	private DownLoadListAdapter myFileAdapter;
	private ListView myList;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		if(this.myFileAdapter !=null){
			this.myFileAdapter.setDatas(this.getFileList());
		}
			super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_download_list,
				container, false);
		bulidView(view);
		return view;
	}

	private List<Object> getFileList() {
		ArrayList<Object> ar = new ArrayList<Object>();
		File dir = Environment
				.getExternalStoragePublicDirectory(DownLoadListFragment.DOWNLOAD_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File[] fil = dir.listFiles();
		if(fil==null){
			
			return null;
		}
		
		for (int i = 0; i < fil.length; i++) {

			ar.add(  new FileItem(fil[i]));
		}

		return ar;

	}

	public void bulidView(View view) {
		if(getFileList() == null){
			
			return;
		}
		
		myFileAdapter = new DownLoadListAdapter(this.getActivity(),
				getFileList());
		myList = (ListView) view.findViewById(R.id.downlistview);
		myList.setAdapter(myFileAdapter);
		myList.setLongClickable(true);
		myList.setOnItemLongClickListener(this);

	}

	@Override
	public void modelDidFinishedLoad(Model<? extends Object> model) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onItemLongClick(AdapterView parent, View view,
			final int position, long id) {
		
		 final DownLoadListAdapter localAdapter = this.myFileAdapter;
		 final FileItem fi = (FileItem) localAdapter.getDatas().get(position);
		
		 new AlertDialog.Builder(this.getActivity())
				.setTitle(getResources().getString(R.string.activity_done_list_fragment_no_matters) + fi.getTitle())
				.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						localAdapter.getDatas().remove(position);
						localAdapter.notifyDataSetChanged();
						if (fi.getFile().exists()) {
							fi.getFile().delete();

						}

					}

				}).setNegativeButton(getResources().getString(R.string.cancle), null).show();

		return true;
	}

}
