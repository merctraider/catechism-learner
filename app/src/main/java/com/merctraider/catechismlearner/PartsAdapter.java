package com.merctraider.catechismlearner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class PartsAdapter extends BaseAdapter {

    String[] partNames;
    int[] progress;
    LayoutInflater layoutInflater;

    PartsAdapter(Context c, String[] partTitles, int[] progressData){
        partNames = partTitles;
        progress = progressData;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return partNames.length;
    }

    @Override
    public Object getItem(int position) {
        return partNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.part_listview_detail, null);
        CheckedTextView partTitleTextView = (CheckedTextView)v.findViewById(R.id.partTitleCheckedTextView);
        boolean checked;

        if(progress[position] == 1){
            checked = true;
        } else {
            checked = false;
        }

        partTitleTextView.setChecked(checked);
        partTitleTextView.setText(partNames[position]);

        return v;
    }
}
