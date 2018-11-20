package com.dimamon.repo;

import com.dimamon.entities.Student;

import java.util.Collection;

/**
 * Created by dimamon on 11/16/16.
 */
public interface StudentDao {
    Collection<Student> retrieveAll();

    Student retrieveById(int id);

    Student remove(int id);

    void update(Student student);

    void add(Student student);
}
