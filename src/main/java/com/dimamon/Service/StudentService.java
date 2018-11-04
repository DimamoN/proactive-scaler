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
    
    @Autowired
    private InfluxDBTool influxDBTool;

    @Autowired
    private ResourcesService resourcesService;

    public Collection<Student> getAll() {
        influxDBTool.measure(0, "getAll", resourcesService.getCpuLoad());
        return this.studentDao.retrieveAll();
    }

    public Student getById(int id) {
        influxDBTool.measure(id, "get", resourcesService.getCpuLoad());
        return this.studentDao.retrieveById(id);
    }

    public Student removeById(int id) {
        influxDBTool.measure(id, "remove", resourcesService.getCpuLoad());
        return this.studentDao.remove(id);
    }

    public void update(final Student student) {
        influxDBTool.measure(student.getId(), "update", resourcesService.getCpuLoad());
        this.studentDao.update(student);
    }

    public void add(final Student student) {
        influxDBTool.measure(student.getId(), "insert", resourcesService.getCpuLoad());
        this.studentDao.add(student);
    }
}
