package com.dimamon.Dao;

import com.dimamon.Entity.Student;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimamon on 11/15/16.
 * в InfluxDB буду записывать соединения
 * а Grafana будет рисовать сумму соединений за время (например за минуту)
 */
@Repository
@Qualifier("fakeData")
public class FakeStudentDao implements StudentDao {

    private static Map<Integer, Student> STUDENTS;

    // fake data
    static {
        STUDENTS = new HashMap<Integer, Student>() {
            {
                put(1, new Student(1, "Said", "Computer Science"));
                put(2, new Student(2, "Alex", "Finance"));
                put(3, new Student(3, "Anna", "Maths"));
                put(4, new Student(4, "Tonya", "Sociology"));
            }
        };
    }

    @Override
    public Collection<Student> retrieveAll() {
        return STUDENTS.values();
    }

    @Override
    public Student retrieveById(int id) {
        return STUDENTS.get(id);
    }

    @Override
    public Student remove(int id) {
        return STUDENTS.remove(id);
    }

    @Override
    public void update(final Student student) {
        Student updStudent = STUDENTS.get(student.getId());
        updStudent.setName(student.getName());
        updStudent.setCourse(student.getCourse());
        STUDENTS.replace(student.getId(), updStudent);
    }

    @Override
    public void add(final Student student) {
        STUDENTS.put(student.getId(), student);
    }
}
