package com.server.auth.service;

import com.server.auth.domain.LoginAttempDomain;
import com.server.auth.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final LoginAttemptRepository repository;

    public LoginService(LoginAttemptRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addLoginAttempts(String email, boolean success) {
        LoginAttempDomain loginAttempt = new LoginAttempDomain(email, success, LocalDateTime.now());
    }

    public List<LoginAttempDomain> findRecentLoginAttempts(String email) {
        return repository.findRecent(email);
    }
}
