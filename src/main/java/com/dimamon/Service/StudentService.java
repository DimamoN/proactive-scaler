package com.dimamon.Service;

import com.dimamon.Dao.StudentDao;
import com.dimamon.Entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by dimamon on 11/15/16.
 */

@Service
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public Collection<Student> getAllStudents(){
        return this.studentDao.getAllStudents();
    }

    //Можно добавить - проверку на существование студента,
    //Если его не существует - отправить Request code - данные не доступны
    public Student getStudentById(int id){
        return this.studentDao.getStudentById(id);
    }

    //Добавить Существует ли студент с таким id
    public Student removeStudentById(int id) {
        return this.studentDao.removeStudentById(id);
    }

    public void updateStudent(Student student) {
        this.studentDao.updateStudent(student);
    }


    public void insertStudent(Student student) {
        this.studentDao.insertStudent(student);
    }
}
