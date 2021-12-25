package com.example.mylists.compare;

import com.example.mylists.model.Student;

import java.util.Comparator;

class CompareByFaculty implements Comparator<Student> {
    @Override
    public int compare(Student t1, Student t2) {
        return t1.getNameFaculty().compareTo(t2.getNameFaculty());
    }
}