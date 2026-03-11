package com.center.manager.repo.jpa;

import com.center.manager.model.Student;
import com.center.manager.repo.StudentRepository;
import jakarta.persistence.EntityManager;

public class JpaStudentRepository implements StudentRepository {

    @Override
    public Student findById(EntityManager em, Long studentId) {
        return em.find(Student.class, studentId);
    }
}

