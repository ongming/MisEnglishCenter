package com.center.manager.repo;

import com.center.manager.model.Student;
import jakarta.persistence.EntityManager;

public interface StudentRepository {
    Student findById(EntityManager em, Long studentId);
}

