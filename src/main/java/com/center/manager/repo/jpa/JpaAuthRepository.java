package com.center.manager.repo.jpa;

import com.center.manager.model.UserAccount;
import com.center.manager.repo.AuthRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class JpaAuthRepository implements AuthRepository {

    @Override
    public UserAccount findByUsername(EntityManager em, String username) {
        TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u WHERE u.username = :username", UserAccount.class);
        query.setParameter("username", username);
        List<UserAccount> results = query.getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }
}

