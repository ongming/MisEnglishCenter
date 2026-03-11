package com.center.manager.repo;

import com.center.manager.model.Teacher;
import jakarta.persistence.EntityManager;

public interface TeacherRepository {
    Teacher findById(EntityManager em, Long teacherId);
}
