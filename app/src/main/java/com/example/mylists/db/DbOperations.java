package com.example.mylists.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mylists.activity.MainActivity;
import com.example.mylists.activity.StudentInfoActivity;
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
            "FOREIGN KEY (" + Student.StudentContract.StudentEntry.ID_FACULTY + ")" +
            " REFERENCES " + Facultet.FacultetContract.FacultetEntry.TABLE_NAME +
            "(" + Facultet.FacultetContract.FacultetEntry.ID + "));";

    private static final String CREATE_FACULTET_TABLE = "create table if not exists " +
            Facultet.FacultetContract.FacultetEntry.TABLE_NAME + "(" +
            Facultet.FacultetContract.FacultetEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Facultet.FacultetContract.FacultetEntry.NAME + " TEXT " + ");";

    private static final String CREATE_SUBJECT_TABLE = "create table if not exists " +
            Subject.SubjectContract.SubjectEntry.TABLE_NAME + "(" +
            Subject.SubjectContract.SubjectEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Subject.SubjectContract.SubjectEntry.NAME + " TEXT ," +
            Subject.SubjectContract.SubjectEntry.ID_STUDENT + " INTEGER NOT NULL " + "," +
            Subject.SubjectContract.SubjectEntry.MARK + " INTEGER ," +
            "FOREIGN KEY (" + Subject.SubjectContract.SubjectEntry.ID_STUDENT + ")" +
            " REFERENCES " + Student.StudentContract.StudentEntry.TABLE_NAME +
            "(" + Student.StudentContract.StudentEntry.ID + "));";

    /**
     * База данных создаётся
     * @param context
     */
    DbOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "Database created...");
    }

    /**
     * Создание таблиц, если их нет
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FACULTET_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_SUBJECT_TABLE);
        Log.d(TAG, "Tables created...");
    }

    /**
     * Добавление факультетов
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
    public void addInfoStudent(SQLiteDatabase db, Student student) {
        if(existStudent(db, student)) {
            updateStudent(db, student);
            Log.d("Database operations", "One row updated...");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Student.StudentContract.StudentEntry.ID_FACULTY, student.getIdFaculty());
            contentValues.put(Student.StudentContract.StudentEntry.FIO, student.getFIO());
            contentValues.put(Student.StudentContract.StudentEntry.GROUP, student.getGroup());
            db.insert(Student.StudentContract.StudentEntry.TABLE_NAME, null, contentValues);
            Log.d(TAG, "One row inserted...");
        }
    }

    /**
     * Добавление инфы о предмете или обновление
     * @param db
     */
    public void addInfoSubject(SQLiteDatabase db, Subject subject) {
        Log.d("addInfoSubject", "start");
        if(existSubject(db, subject)) {
            updateSubject(db, subject);
            Log.d("addInfoSubject", "update");
        } else {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Subject.SubjectContract.SubjectEntry.ID_STUDENT, subject.getStudentId());
            contentValues.put(Subject.SubjectContract.SubjectEntry.NAME, subject.getName());
            contentValues.put(Subject.SubjectContract.SubjectEntry.MARK, subject.getMark());
            db.insert(Subject.SubjectContract.SubjectEntry.TABLE_NAME, null, contentValues);
            Log.d("addInfoSubject", "insert");
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
     * Удаление предмета
     * @param db
     */
    public void deleteSubject(SQLiteDatabase db, Subject subject) {
        if(existSubject(db, subject)) {
            db.delete(
                    Subject.SubjectContract.SubjectEntry.TABLE_NAME,
                    Subject.SubjectContract.SubjectEntry.ID + " = ?",
                    new String[]{String.valueOf(subject.getId())});
        }
    }

    /**
     * Обновление предмета
     * @param db
     */
    public void updateSubject(SQLiteDatabase db, Subject subject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Subject.SubjectContract.SubjectEntry.ID_STUDENT, subject.getStudentId());
        contentValues.put(Subject.SubjectContract.SubjectEntry.NAME, subject.getName());
        contentValues.put(Subject.SubjectContract.SubjectEntry.MARK, subject.getMark());
        db.update(Subject.SubjectContract.SubjectEntry.TABLE_NAME, contentValues, Subject.SubjectContract.SubjectEntry.ID + " = ?", new String[] {String.valueOf(subject.getId())});
    }

    /**
     * Обновление студента
     * @param db
     * @param student
     */
    public void updateStudent(SQLiteDatabase db, Student student) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Student.StudentContract.StudentEntry.ID_FACULTY, student.getIdFaculty());
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
                        Student.StudentContract.StudentEntry.ID_FACULTY,
                        Student.StudentContract.StudentEntry.GROUP
        };
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MainActivity.mStudents.clear();
        String selection = Student.StudentContract.StudentEntry.ID_FACULTY + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id_faculty)};
        /**
         * имя таблицы, что достаём, условие, аргументы подставляемые в условие
         */
        Cursor cursor = db.query(Student.StudentContract.StudentEntry.TABLE_NAME, projections,
                selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.ID));
            @SuppressLint("Range") String fio = cursor.getString(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.FIO));
            @SuppressLint("Range") String group = cursor.getString(
                    cursor.getColumnIndex(Student.StudentContract.StudentEntry.GROUP));
            Student student = new Student(fio, getFacultetById(db, id_faculty), group);
            student.setId(id);
            MainActivity.mStudents.add(student);
        }
    }

    /**
     * Получение списка предметов по id студента
     * @param db
     */
    public void getAllSubjects(SQLiteDatabase db, int id_student) {
        String[] projections = {
                Subject.SubjectContract.SubjectEntry.ID,
                Subject.SubjectContract.SubjectEntry.NAME,
                Subject.SubjectContract.SubjectEntry.ID_STUDENT,
                Subject.SubjectContract.SubjectEntry.MARK,
        };
        StudentInfoActivity.mSubjects.clear();
        String selection = Subject.SubjectContract.SubjectEntry.ID_STUDENT + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(id_student)};
        /**
         * имя таблицы, что достаём, условие, аргументы подставляемые в условие
         */
        Cursor cursor = db.query(Subject.SubjectContract.SubjectEntry.TABLE_NAME, projections,
                selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(
                    cursor.getColumnIndex(Subject.SubjectContract.SubjectEntry.ID));
            @SuppressLint("Range") String name = cursor.getString(
                    cursor.getColumnIndex(Subject.SubjectContract.SubjectEntry.NAME));
            @SuppressLint("Range") Integer mark = cursor.getInt(
                    cursor.getColumnIndex(Subject.SubjectContract.SubjectEntry.MARK));
            Subject subject = new Subject(id_student, name, mark);
            subject.setId(id);
            StudentInfoActivity.mSubjects.add(subject);
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

    /**
     * Проверка существует ли оценка по предмету
     * @param db
     * @return
     */
    public boolean existSubject(SQLiteDatabase db, Subject subject) {
        String[] projections = {
                Subject.SubjectContract.SubjectEntry.ID
        };
        String selection =  Subject.SubjectContract.SubjectEntry.ID + "= ?";
        String [] selectionArgs = new String[] {String.valueOf(subject.getId())};
        Cursor cursor = db.query( Subject.SubjectContract.SubjectEntry.TABLE_NAME,
                projections,selection,selectionArgs,
                null,null,null );
        return cursor.getCount() > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
