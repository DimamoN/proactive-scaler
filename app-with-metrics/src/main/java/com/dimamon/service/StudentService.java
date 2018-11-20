package com.dimamon.service;

import com.dimamon.entities.Student;
import com.dimamon.repo.MeasurementsRepo;
import com.dimamon.repo.StudentDao;
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
    
    @Autowired
    private MeasurementsRepo measurementsRepo;

    public Collection<Student> getAll() {
        measurementsRepo.measureConnection(0, "getAll");
        return this.studentDao.retrieveAll();
    }

    public Student getById(int id) {
        measurementsRepo.measureConnection(id, "get");
        return this.studentDao.retrieveById(id);
    }

    public Student removeById(int id) {
        measurementsRepo.measureConnection(id, "remove");
        return this.studentDao.remove(id);
    }

    public void update(final Student student) {
        measurementsRepo.measureConnection(student.getId(), "update");
        this.studentDao.update(student);
    }

    public void add(final Student student) {
        measurementsRepo.measureConnection(student.getId(), "insert");
        this.studentDao.add(student);
    }
}
