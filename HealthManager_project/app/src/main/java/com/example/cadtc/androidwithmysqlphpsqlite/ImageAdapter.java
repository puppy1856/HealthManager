package com.example.cadtc.androidwithmysqlphpsqlite;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import library.UserFunctions;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	

	ArrayList<String> rank = new ArrayList<>(); //排行榜帳號
	ArrayList<String> score = new ArrayList<>(); //排行榜分數



	// Constructor
	public ImageAdapter(Context c){
		mContext = c;
		UserFunctions userFunctions = new UserFunctions();
		JSONObject rt = userFunctions.getdatarank();
		try
		{
			rank.add(rt.getString("rank1_name"));
			rank.add(rt.getString("rank2_name"));
			rank.add(rt.getString("rank3_name"));
			rank.add(rt.getString("rank4_name"));
			rank.add(rt.getString("rank5_name"));
			score.add(rt.getString("rank1_flower"));
			score.add(rt.getString("rank2_flower"));
			score.add(rt.getString("rank3_flower"));
			score.add(rt.getString("rank4_flower"));
			score.add(rt.getString("rank5_flower"));
		}
		catch(Exception e){}
	}

	@Override
	public int getCount() {
		return rank.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater =  LayoutInflater.from(mContext);
		if (convertView == null) {
	         convertView = inflater.inflate(R.layout.listview_layout, null, false);
		}
	      
		TextView a = (TextView)convertView.findViewById(R.id.rank);
		TextView b = (TextView)convertView.findViewById(R.id.score);
		TextView c = (TextView)convertView.findViewById(R.id.number);
		a.setText(rank.get(position));
		b.setText(score.get(position));
		c.setText(String.valueOf(position+1));
        return convertView;
	}

}
