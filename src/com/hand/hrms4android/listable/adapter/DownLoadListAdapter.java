package com.hand.hrms4android.listable.adapter;

import java.io.File;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hand.hrms4android.R;
import com.hand.hrms4android.listable.item.FileItem;
import com.hand.hrms4android.mimetype.MimetypeGet;

public class DownLoadListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Object> datas;
	private Context	context;

	public DownLoadListAdapter(Context context, List<Object> datas) {
		this.datas = datas;
		this.context = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return datas.get(position).hashCode();
	}

	public List<Object> getDatas() {
		return datas;

	}

	public void setDatas(List<Object> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();

	}

	public void setImageView(ImageView iv, String end) {
		if (end.equals("jpg") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			iv.setImageResource(R.drawable.jpg_show);
		} else if (end.equals("doc") || end.equals("docx")) {
			iv.setImageResource(R.drawable.doc_show);
		} else if (end.equals("pdf")) {
			iv.setImageResource(R.drawable.pdf_show);
		} else if (end.equals("pptx") || end.equals("ppt")) {
			iv.setImageResource(R.drawable.ppt_show);

		} else if (end.equals("xls") || end.equals("xlsx")) {

			iv.setImageResource(R.drawable.excel_item);

		} else if (end.equals("txt")) {
			iv.setImageResource(R.drawable.txt_show);

		} else {

			iv.setImageResource(R.drawable.other_show);
		}

	}
	public FileItem getFileItem(File file){
		return new FileItem(file);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		FileItem fi = (FileItem) datas.get(position);
		View row = mInflater
				.inflate(R.layout.download_list_item, parent, false);
		TextView tvN = (TextView) row.findViewById(R.id.dliFileName);
		TextView tvS = (TextView) row.findViewById(R.id.dliFileSize);
		ImageView tvI = (ImageView) row.findViewById(R.id.dliIm);


		Button btn = (Button) row.findViewById(R.id.dlibutton);
		btn.setTag(position);
		tvN.setText(fi.getTitle());
		tvS.setText(fi.getSize());
		setImageView(tvI, fi.getEnd());
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = Integer.parseInt(v.getTag().toString());

				FileItem fi = (FileItem) datas.get(position);
				File file = fi.getFile();

				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				
				// get the mimetype of the file
				String type = MimetypeGet.getByFileName(fi.getTitle());

				// //设置intent的data和Type属性。
				intent.setDataAndType(/* uri */Uri.fromFile(file), type);
				// //跳转
				try {
					context.startActivity(intent);
				} catch (ActivityNotFoundException exception) {
					Log.wtf("mimetype","can not handle the mimetype");
				}

			}

		});


		return row;

	}


}