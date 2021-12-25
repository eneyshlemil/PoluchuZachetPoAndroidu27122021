package com.example.mylists.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mylists.R;
import com.example.mylists.model.Subject;

import java.util.ArrayList;

public class SubjectListAdapter extends BaseAdapter {
    ArrayList<Subject> mSubjects;
    Context mContext;
    LayoutInflater mInflater;

    public SubjectListAdapter(ArrayList<Subject> subjects, Context context) {
        mSubjects = subjects;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mSubjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mSubjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.subject_element, parent, false);
        if (mSubjects.isEmpty()) return convertView;

        ((TextView) convertView.findViewById(R.id.tvSubjeсtName)).setText(mSubjects.get(position).getName());
        ((TextView) convertView.findViewById(R.id.tvSubjectMark)).setText(mSubjects.get(position).getMark().toString());

        ((TextView) convertView.findViewById(R.id.tvSubjectMark)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
  //                      showPopupMenu(view,position);
                    }
                }
        );

        return convertView;
    }

    public void colorChecked(int position, AdapterView<?> parent){
        View view;
        ListView listView = parent.findViewById(R.id.lvASI_Subjects);
        for (int i = 0; i < mSubjects.size(); ++i){
            view = parent.getChildAt(i);
            ((LinearLayout) view.findViewById(R.id.llElementSub)).setBackgroundColor(
                    mContext.getResources().getColor(R.color.white));
        }
        view = parent.getChildAt(position);
        if(listView.isSelected())
            ((LinearLayout) view.findViewById(R.id.llElementSub)).setBackgroundColor(
                    mContext.getResources().getColor(R.color.checked_element));
    }
}
