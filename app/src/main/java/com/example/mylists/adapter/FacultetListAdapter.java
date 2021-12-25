package com.example.mylists.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mylists.model.Facultet;

import java.util.ArrayList;


public class FacultetListAdapter extends ArrayAdapter<Facultet> {
   private Context context;
   private ArrayList<Facultet> facultets = new ArrayList<>();

   public FacultetListAdapter(Context context, int textViewResourceId, ArrayList<Facultet> facultets) {
       super(context, textViewResourceId, facultets);
       this.context = context;
       this.facultets = facultets;
   }
   @Override
    public int getCount() {
       return facultets.size();
   }
   @Override
    public Facultet getItem(int position) {
       return facultets.get(position);
   }
   @Override
    public long getItemId(int position) {
       return position;
   }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(facultets.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(facultets.get(position).getName());

        return label;
    }
}
