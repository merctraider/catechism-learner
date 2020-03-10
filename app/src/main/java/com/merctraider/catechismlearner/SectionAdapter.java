package com.merctraider.catechismlearner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SectionAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    String[] progress;
    String[] sections;
    Drawable[] iconLook;

    SectionAdapter(Context c, String[] sectionTitles, String[] progressStrings, Drawable[] imgIcons){

        sections = sectionTitles;
        progress = progressStrings;
        iconLook = imgIcons;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return sections.length;
    }

    @Override
    public Object getItem(int position) {
        return sections[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mInflater.inflate(R.layout.main_listview_detail, null);
        TextView sectionTitleTextView = (TextView) v.findViewById(R.id.sectionTitleTextView);
        ImageView iconImageView = (ImageView) v.findViewById(R.id.iconImageView);
        TextView progressTextView = (TextView) v.findViewById(R.id.progressTextView);

        String sectionTitle = sections[position];
        String progressString = progress[position];
        Drawable iconImg = iconLook[position];

        sectionTitleTextView.setText(sectionTitle);
        progressTextView.setText(progressString);
        iconImageView.setImageDrawable(iconImg);


        return v;
    }
}
