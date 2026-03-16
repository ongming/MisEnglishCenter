package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.AuthRepository;
import com.center.manager.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Implement AuthService bang repository JPA.
 */
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepo;
    private final TransactionManager tx;

    public AuthServiceImpl(AuthRepository authRepo, TransactionManager tx) {
        this.authRepo = authRepo;
        this.tx = tx;
    }

    /**
     * Validate tai khoan va password hash trong 1 transaction read-only.
     */
    @Override
    public Object[] login(String username, String rawPassword) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(authRepo.findByUsername(em, username))
                        .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                        .filter(u -> BCrypt.checkpw(rawPassword, u.getPasswordHash()))
                        .map(u -> new Object[]{
                                u.getUserId(), u.getUsername(), u.getRole(),
                                u.getTeacherId(), u.getStudentId(), u.getStaffId()
                        })
                        .orElse(null));
    }
}

