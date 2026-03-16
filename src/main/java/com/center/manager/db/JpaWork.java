package com.center.manager.db;

import jakarta.persistence.EntityManager;

/**
 * Functional interface đại diện cho một tác vụ thao tác với EntityManager.
 * Giúp truyền lambda hoặc method reference để thực thi các truy vấn JPA một cách linh hoạt.
 *
 * <T>: Kiểu dữ liệu trả về của tác vụ (có thể là entity, list, hoặc bất kỳ object nào).
 *
 * Ví dụ sử dụng:
 * JpaWork<List<Student>> work = em -> em.createQuery("from Student").getResultList();
 * List<Student> students = jpa.execute(work);
 */
@FunctionalInterface
public interface JpaWork<T> {
    /**
     * Thực thi tác vụ với EntityManager.
     * @param em EntityManager để thao tác với database
     * @return Kết quả trả về (tùy ý)
     * @throws Exception nếu có lỗi khi thực thi
     */
    T execute(EntityManager em) throws Exception;
}
