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
import com.example.mylists.model.Student;

import java.util.ArrayList;

public class StudentListAdapter extends BaseAdapter {
    ArrayList<Student> mStudents = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;

    public StudentListAdapter(ArrayList<Student> students, Context context) {
        mStudents = students;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() { return mStudents.size(); }

    @Override
    public Object getItem(int position){ return mStudents.get(position); }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View view, ViewGroup parent){
        view = mInflater.inflate(R.layout.student_element, parent, false);
        if (mStudents.isEmpty()) return view;
        ((TextView) view.findViewById(R.id.tvElementFIO)).setText(mStudents.get(position).getFIO());
        ((TextView) view.findViewById(R.id.tvElementFaculty)).setText(mStudents.get(position).getNameFaculty());
        ((TextView) view.findViewById(R.id.tvElementGroup)).setText(mStudents.get(position).getGroup());
        ((TextView) view.findViewById(R.id.tvTelephone)).setText(mStudents.get(position).getTelephone());
        if(position%2==1) ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
                mContext.getResources().getColor(R.color.odd_element)
        );
        return view;
    }

    public void colorChecked(int position, AdapterView<?> parent){
        View view;
        ListView listView = parent.findViewById(R.id.lvList2);
        for (int i = 0; i < mStudents.size(); ++i){
            view = parent.getChildAt(i);
            if (i % 2 == 1)
                ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
                        mContext.getResources().getColor(R.color.odd_element));
            else ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
                    mContext.getResources().getColor(R.color.white));

        }
        view = parent.getChildAt(position);
        if(listView.isSelected())
            ((LinearLayout) view.findViewById(R.id.llElement)).setBackgroundColor(
                    mContext.getResources().getColor(R.color.checked_element));
    }
}
