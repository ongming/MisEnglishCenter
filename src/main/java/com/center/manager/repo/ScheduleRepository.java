package com.center.manager.repo;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ScheduleRepository {
    List<Object[]> findByTeacher(EntityManager em, Long teacherId);
    List<Object[]> findByStudent(EntityManager em, Long studentId);
}

