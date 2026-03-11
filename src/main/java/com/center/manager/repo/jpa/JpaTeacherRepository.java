package com.center.manager.repo.jpa;

import com.center.manager.model.Teacher;
import com.center.manager.repo.TeacherRepository;
import jakarta.persistence.EntityManager;

public class JpaTeacherRepository implements TeacherRepository {

    @Override
    public Teacher findById(EntityManager em, Long teacherId) {
        return em.find(Teacher.class, teacherId);
    }
}
