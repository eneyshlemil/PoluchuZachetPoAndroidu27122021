package com.example.mylists.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Comparator;

public class BackgroundTask extends AsyncTask<String, Void, String> {
    Context context;
    public BackgroundTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        DbOperations dbOperations = new DbOperations(context);
        String method = strings[0];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        if(method.equals("add_info")) {
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Student student = gson.fromJson(strings[1], Student.class);
            System.out.println("student from json: " + student);
            dbOperations.addInfo(db, student);
        }
        else if(method.equals("delete_student")) {
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Student student = gson.fromJson(strings[1], Student.class);
            System.out.println("student from json before delete: " + student);
            dbOperations.deleteStudent(db, student);
        }
        else if(method.equals("get_students")) {
            SQLiteDatabase db = dbOperations.getReadableDatabase();
            int facultyId = Integer.parseInt(strings[1]);
            dbOperations.getAllStudents(db, facultyId);
        }
        else if(method.equals("get_facultets")) {
            SQLiteDatabase db = dbOperations.getReadableDatabase();
            dbOperations.addFacultets(db);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity.loadFacultyIntoNavigationView();
        MainActivity.mStudentListAdapter.notifyDataSetChanged();
    }
}
