package com.example.mylists.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

public class Student implements Parcelable {
    private int id;
    private String mFIO;
    private Integer IdFaculty;
    private String nameFaculty;
    private String mGroup;
    private ArrayList<Subject> mSubjects;

    public Student(String FIO, Facultet faculty, String group) {
        mFIO = FIO;
        nameFaculty = faculty.getName();
        IdFaculty = faculty.getId();
        mGroup = group;
        mSubjects = new ArrayList<>();
        id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Сравнение студентов
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && mFIO.equals(student.mFIO) && IdFaculty.equals(student.IdFaculty) && nameFaculty.equals(student.nameFaculty) && mGroup.equals(student.mGroup) && Objects.equals(mSubjects, student.mSubjects);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, mFIO, IdFaculty, nameFaculty, mGroup, mSubjects);
    }

    protected Student(Parcel in) {
        mFIO = in.readString();
        nameFaculty = in.readString();
        IdFaculty = in.readInt();
        mGroup = in.readString();
        mSubjects = in.createTypedArrayList(Subject.CREATOR);
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFIO);
        dest.writeString(nameFaculty);
        dest.writeInt(IdFaculty);
        dest.writeString(mGroup);
        dest.writeTypedList(mSubjects);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getFIO() {
        return mFIO;
    }

    public Integer getIdFaculty() {
        return IdFaculty;
    }

    public void setIdFaculty(Integer idFaculty) {
        IdFaculty = idFaculty;
    }

    public String getNameFaculty() {
        return nameFaculty;
    }

    public void setNameFaculty(String nameFaculty) {
        this.nameFaculty = nameFaculty;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setFIO(String FIO) {
        mFIO = FIO;
    }



    public void setGroup(String group) {
        mGroup = group;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        mSubjects = subjects;
    }

    public ArrayList<Subject> getSubjects() {
        return mSubjects;
    }

    public int addSubject(Subject subject){
        mSubjects.add(subject);
        return mSubjects.size();
    }

    public int changeSubject(int position, String name, int mark){
        mSubjects.get(position).setName(name);
        mSubjects.get(position).setMark(mark);
        return mSubjects.size();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", mFIO='" + mFIO + '\'' +
                ", IdFaculty=" + IdFaculty +
                ", nameFaculty='" + nameFaculty + '\'' +
                ", mGroup='" + mGroup + '\'' +
                ", mSubjects=" + mSubjects +
                '}';
    }
    public Student() {
        mFIO = "";
        nameFaculty = "";
        IdFaculty = -1;
        mGroup = "";
        mSubjects = new ArrayList<>();
        id = -1;
    }

    public static final class StudentContract {
        // Student - Faculty N:1
        public static abstract class StudentEntry {
            public static final String ID = "id";
            public static final String ID_FACULTY = "id_faculty";
            public static final String GROUP = "student_group";
            public static final String FIO = "fio";
            // JSON-строка
            public static final String SUBJECTS = "subjects";
            public static final String TABLE_NAME = "student_table";
        }
        // N : N connection
       /* public static abstract class StudentSubjectEntry {
            public static final String ID_STUDENT = "id_student";
            public static final String ID_SUBJECT = "id_subject";
            public static final String
        }*/
    }
}
