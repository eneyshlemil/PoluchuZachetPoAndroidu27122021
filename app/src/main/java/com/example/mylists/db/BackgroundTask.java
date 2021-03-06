package com.example.mylists.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.model.Student;
import com.example.mylists.model.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BackgroundTask extends AsyncTask<String, Void, String> {
    @SuppressLint("StaticFieldLeak")
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
            Log.d("Add", student.getFIO());
            dbOperations.addInfoStudent(db, student);
        }
        else if(method.equals("delete_student")) {
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Student student = gson.fromJson(strings[1], Student.class);
            Log.d("Delete", student.getFIO());
            dbOperations.deleteStudent(db, student);
        }
        else if(method.equals("get_students")) {
            SQLiteDatabase db = dbOperations.getReadableDatabase();
            int facultyId = Integer.parseInt(strings[1]);
            Log.d("Get ", "students");
            dbOperations.getAllStudents(db, facultyId);
        }
        else if(method.equals("get_facultets")) {
            SQLiteDatabase db = dbOperations.getReadableDatabase();
            Log.d("Get ", "facultets");
            dbOperations.addFacultets(db);
        }
        else if(method.equals("get_subjects")) {
            SQLiteDatabase db = dbOperations.getReadableDatabase();
            int studentId = Integer.parseInt(strings[1]);
            Log.d("Get ", "students");
            dbOperations.getAllSubjects(db, studentId);
        }
        else if(method.equals("add_subject_info")) {
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Subject subject = gson.fromJson(strings[1], Subject.class);
            Log.d("Add", subject.getName());
            dbOperations.addInfoSubject(db, subject);
        }
        else if(method.equals("delete_subject")) {
            SQLiteDatabase db = dbOperations.getWritableDatabase();
            Subject subject = gson.fromJson(strings[1], Subject.class);
            Log.d("Delete", subject.getName());
            dbOperations.deleteSubject(db, subject);
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
