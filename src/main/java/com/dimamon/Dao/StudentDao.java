package com.dimamon.Dao;

import com.dimamon.Entity.Student;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimamon on 11/15/16.
 */

@Repository
public class StudentDao {

    private static Map<Integer,Student> students;

    static {
        students = new HashMap<Integer,Student>(){

            {
                put(1,new Student(1,"Said","Computer Science"));
                put(2,new Student(2,"Alex","Finance"));
                put(3,new Student(3,"Anna","Maths"));
                put(4,new Student(4,"Tonya","Sociology"));
            }
        };
    }

    public Collection<Student> getAllStudents(){
        return this.students.values();
    }

    public Student getStudentById(int id){
        return this.students.get(id);
    }

    public Student removeStudentById(int id) {
        return this.students.remove(id);
    }

    public void updateStudent(Student student){

        Student updStudent = this.students.get(student.getId());

        updStudent.setName(student.getName());
        updStudent.setCourse(student.getCourse());

        students.replace(student.getId(), updStudent);
    }

    public void insertStudent(Student student) {
        this.students.put(student.getId(),student);
    }
}
