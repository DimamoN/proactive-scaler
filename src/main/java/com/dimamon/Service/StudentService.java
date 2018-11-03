package com.dimamon.Service;

import com.dimamon.Dao.InfluxDBTool;
import com.dimamon.Dao.StudentDao;
import com.dimamon.Entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by dimamon on 11/15/16.
 */
@Service
public class StudentService {

    @Autowired
    @Qualifier("fakeData")
    private StudentDao studentDao;

    //TEST
    @Autowired
    private InfluxDBTool influxDBTool;

    public Collection<Student> getAllStudents() {
        influxDBTool.measure(0, "getAll");
        return this.studentDao.getAllStudents();
    }

    //Можно добавить - проверку на существование студента,
    //Если его не существует - отправить Request code - данные не доступны
    public Student getStudentById(int id) {
        influxDBTool.measure(id, "get");
        return this.studentDao.getStudentById(id);
    }

    //Добавить Существует ли студент с таким id
    public Student removeStudentById(int id) {
        influxDBTool.measure(id, "remove");
        return this.studentDao.removeStudentById(id);
    }

    public void updateStudent(Student student) {
        influxDBTool.measure(student.getId(), "update");
        this.studentDao.updateStudent(student);
    }

    public void insertStudent(Student student) {
        influxDBTool.measure(student.getId(), "insert");
        this.studentDao.insertStudent(student);
    }
}
