package com.dimamon.Dao;

import com.dimamon.Entity.Student;

import java.util.Collection;

/**
 * Created by dimamon on 11/16/16.
 */
public interface StudentDao {
    Collection<Student> getAllStudents();

    Student getStudentById(int id);

    Student removeStudentById(int id);

    void updateStudent(Student student);

    void insertStudent(Student student);
}
