package com.example.mylists.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Facultet implements Parcelable {
    private String name;
    private int id;

    public static final Creator<Facultet> CREATOR = new Creator<Facultet>() {
        @Override
        public Facultet createFromParcel(Parcel in) {
            return new Facultet(in);
        }

        @Override
        public Facultet[] newArray(int size) {
            return new Facultet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }

    public Facultet(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected Facultet(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static final class FacultetContract {
      public static abstract class FacultetEntry {
         public static final String ID = "id";
         public static final String NAME = "name";
         public static final String TABLE_NAME = "facultet_table";
      }
    }
}
