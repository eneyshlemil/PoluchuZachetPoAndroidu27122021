package com.example.mylists.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.model.Facultet;
import com.example.mylists.model.Student;
import com.example.mylists.model.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

public class DbOperations extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private final String TAG = "DB operation";

    private static final String DB_NAME = "students.db";

    private static final String CREATE_STUDENT_TABLE = "create table if not exists " +
            Student.StudentContract.StudentEntry.TABLE_NAME + "(" +
            Student.StudentContract.StudentEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Student.StudentContract.StudentEntry.FIO + " TEXT ," +
            Student.StudentContract.StudentEntry.ID_FACULTY + " INTEGER NOT NULL " + "," +
            Student.StudentContract.StudentEntry.GROUP + " TEXT ," +
            Student.StudentContract.StudentEntry.SUBJECTS + " TEXT, " +
            "FOREIGN KEY (" + Student.StudentContract.StudentEntry.ID_FACULTY + ")" +
            " REFERENCES " + Facultet.FacultetContract.FacultetEntry.TABLE_NAME +
            "(" + Facultet.FacultetContract.FacultetEntry.ID + "));";

    private static final String CREATE_FACULTET_TABLE = "create table if not exists " +
            Facultet.FacultetContract.FacultetEntry.TABLE_NAME + "(" +
            Facultet.FacultetContract.FacultetEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Facultet.FacultetContract.FacultetEntry.NAME + " TEXT " + ");";

    DbOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "Database created...");
    }

    /**
     * При создании создаём таблички, если их нет
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FACULTET_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        Log.d(TAG, "Tables created...");
    }

    /**
     * Добавление факультет
     * @param db
     */
    public void addFacultets(SQLiteDatabase db) {
        if(isEmptyFacultyTable(db)) {
            MainActivity.mFacultets.clear();
            MainActivity.mFacultets.add(new Facultet("ФКТиПМ", 1));
            MainActivity.mFacultets.add(new Facultet("ФИСМО", 2));
            MainActivity.mFacultets.add(new Facultet("Матфак", 3));
            MainActivity.mFacultets.add(new Facultet("Юрфак", 4));
            MainActivity.mFacultets.add(new Facultet("ФУП", 5));
            System.out.println("isEmptyFacultyTable(db) == true");
            for (Facultet facultet : MainActivity.mFacultets) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Facultet.FacultetContract.FacultetEntry.NAME, facultet.getName());
                db.insert(Facultet.FacultetContract.FacultetEntry.TABLE_NAME, null, contentValues);
            }
            Log.d(TAG, "Facultets inserted into empty " +
                    Facultet.FacultetContract.FacultetEntry.TABLE_NAME + "...");
        } else {
            Log.d(TAG, "isEmptyFacultyTable(db) == false");
            getAllFacultets(db);
        }
    }

    /**
     * Проверка является ли таблица факультетов пустой
     * @param db
     * @return
     */
    public boolean isEmptyFacultyTable(SQLiteDatabase db) {
        String[] projections = {
                Facultet.FacultetContract.FacultetEntry.ID
        };

        Cursor cursor = db.query(Facultet.FacultetContract.FacultetEntry.TABLE_NAME,
                projections,null, null,
                null,null,null );
        return cursor.getCount() == 0;
    }

    /**
     * Добавление инфы о студенте или обновление
     * @param db
     * @param student
     */
    public void addInfo(SQLiteDatabase db, Student student) {
        if(existStudent(db, student)) {
            updateStudent(db, student);
            Log.d("Database operations", "One row updated...");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ContentValues contentValues = new ContentValues();
            System.out.println("student from json addInfo " + student);
            contentValues.put(Student.StudentContract.StudentEntry.ID_FACULTY, student.getIdFaculty());
            contentValues.put(Student.StudentContract.StudentEntry.SUBJECTS, gson.toJson(student.getSubjects()));
            contentValues.put(Student.StudentContract.StudentEntry.FIO, student.getFIO());
            contentValues.put(Student.StudentContract.StudentEntry.GROUP, student.getGroup());
            db.insert(Student.StudentContract.StudentEntry.TABLE_NAME, null, contentValues);
            Log.d(TAG, "One row inserted...");
        }
    }

    /**
     * Удаление студента
     * @param db
     * @param student
     */
    public void deleteStudent(SQLiteDatabase db, Student student) {
        if(existStudent(db, student)) {
            db.delete(
                    Student.StudentContract.StudentEntry.TABLE_NAME,
                    Student.StudentContract.StudentEntry.ID + " = ?",
                    new String[]{String.valueOf(student.getId())});
        }
    }

    /**
     * Обновление студента
     * @param db
     * @param student
     */
    public void updateStudent(SQLiteDatabase db, Student student) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Student.StudentContract.StudentEntry.ID_FACULTY, student.getIdFaculty());
        contentValues.put(Student.StudentContract.StudentEntry.SUBJECTS, gson.toJson(student.getSubjects()));
        contentValues.put(Student.StudentContract.StudentEntry.FIO, student.getFIO());
        contentValues.put(Student.StudentContract.StudentEntry.GROUP, student.getGroup());
        db.update(Student.StudentContract.StudentEntry.TABLE_NAME, contentValues, Student.StudentContract.StudentEntry.ID + " = ?", new String[] {String.valueOf(student.getId())});
    }

    /**
     * Получение факультета по id
     * @param db
     * @param id
     * @return
     */
    @SuppressLint("Range")
    public Facultet getFacultetById(SQLiteDatabase db, int id) {
        String[] projections = {
                Facultet.FacultetContract.FacultetEntry.ID,
                Facultet.FacultetContract.FacultetEntry.NAME
        };
        String selection = Facultet.FacultetContract.FacultetEntry.ID + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = db.query(Facultet.FacultetContract.FacultetEntry.TABLE_NAME,
                projections,selection,selectionArgs,
                null,null,null );
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Facultet.FacultetContract.FacultetEntry.NAME));
        return new Facultet(name, id);
    }

    /**
     * Получение списка студентов по id факультета
     * @param db
     * @param id_faculty
     */
    public void getAllStudents(SQLiteDatabase db, int id_faculty) {
        String[] projections = {
                Student.StudentContract.StudentEntry.ID,
                Student.StudentContract.StudentEntry.FIO,
                        Student.StudentContract.StudentEntry.SUBJECTS,
                        Student.StudentContract.StudentEntry.ID_FACULTY,
                        Student.StudentContract.StudentEntry.GROUP
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mStudents.clear();
        String selection = Student.StudentContract.StudentEntry.ID_FACULTY + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id_faculty)};
        Cursor cursor = db.query(Student.StudentContract.StudentEntry.TABLE_NAME, projections,
                selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.ID));
            @SuppressLint("Range") String fio = cursor.getString(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.FIO));
            @SuppressLint("Range") ArrayList<Subject> subjects = gson.fromJson(
                    cursor.getString(
                            cursor.getColumnIndex(
                                    Student.StudentContract.StudentEntry.SUBJECTS)),
                    new TypeToken<ArrayList<Subject>>() {
                    }.getType()
            );
            @SuppressLint("Range") String group = cursor.getString(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.GROUP));
            Student student = new Student(fio, getFacultetById(db, id_faculty), group);
            student.setSubjects(subjects);
            student.setId(id);
            MainActivity.mStudents.add(student);
        }
    }

    /**
     * Получение списка факультетов
     *
     * @param db
     */
    public void getAllFacultets(SQLiteDatabase db) {
        String[] projections = {
                Facultet.FacultetContract.FacultetEntry.ID,
                Facultet.FacultetContract.FacultetEntry.NAME
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mFacultets.clear();
        Cursor cursor = db.query(Facultet.FacultetContract.FacultetEntry.TABLE_NAME, projections,
                null,null,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Facultet.FacultetContract.FacultetEntry.ID));
            @SuppressLint("Range") String name = cursor.getString(
                    cursor.getColumnIndex(Facultet.FacultetContract.FacultetEntry.NAME));

            Facultet facultet = new Facultet(name, id);
            MainActivity.mFacultets.add(facultet);
        }

    }

    /**
     * Проверка существует ли студент
     * @param db
     * @param student
     * @return
     */
    public boolean existStudent(SQLiteDatabase db, Student student) {
        String[] projections = {
                Student.StudentContract.StudentEntry.ID
        };
        String selection = Student.StudentContract.StudentEntry.ID + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(student.getId())};
        Cursor cursor = db.query(Student.StudentContract.StudentEntry.TABLE_NAME,
                projections,selection,selectionArgs,
                null,null,null );
        return cursor.getCount() > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
