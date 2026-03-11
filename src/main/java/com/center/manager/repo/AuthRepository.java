package com.center.manager.repo;

import com.center.manager.model.UserAccount;
import jakarta.persistence.EntityManager;

public interface AuthRepository {
    UserAccount findByUsername(EntityManager em, String username);
}

