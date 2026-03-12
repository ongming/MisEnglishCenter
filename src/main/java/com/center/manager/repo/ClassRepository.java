package com.center.manager.repo;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ClassRepository {
    List<Object[]> findAllClasses(EntityManager em);
    List<Object[]> findClassesByTeacher(EntityManager em, Long teacherId);
    List<Object[]> findStudentsInClass(EntityManager em, Long classId);
    List<Object[]> findClassesByStudent(EntityManager em, Long studentId);
}

