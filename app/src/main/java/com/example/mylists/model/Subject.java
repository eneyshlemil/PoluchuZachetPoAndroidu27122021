package com.example.mylists.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {
    String mName;
    Integer mMark;
    Integer mId;
    Integer mStudentId;

    public Subject(Integer studentId,String name, Integer mark) {
        mName = name;
        mMark = mark;
        mStudentId = studentId;
        mId = -1;
    }

    protected Subject(Parcel in) {
        mName = in.readString();
        if (in.readByte() == 0) {
            mMark = null;
        } else {
            mMark = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        if (mMark == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mMark);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public void setName(String name) {
        mName = name;
    }

    public void setMark(Integer mark) {
        mMark = mark;
    }

    public String getName() {
        return mName;
    }

    public Integer getMark() {
        return mMark;
    }

    public Integer getId() {
        return mId;
    }

    public Integer getStudentId() {
        return mStudentId;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "mName='" + mName + '\'' +
                ", mMark=" + mMark +
                '}';
    }
    public static final class SubjectContract {
        public static abstract class SubjectEntry {
            public static final String ID = "id";
            public static final String ID_STUDENT = "id_student";
            public static final String NAME = "name";
            public static final String MARK = "mark";
            public static final String TABLE_NAME = "subject_table";
        }
    }
}
