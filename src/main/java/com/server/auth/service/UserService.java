package com.server.auth.service;


import com.server.auth.controller.dto.SignupRequestDto;
import com.server.auth.domain.UserDomain;
import com.server.auth.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequestDto request) {
        String email = request.email();

        Optional<UserDomain> existingUser = repository.findByEmail(email);


        // handling duplicate data
        if(existingUser.isPresent()) {
            throw new DuplicateRequestException(String.format("User with the email address '%s' already exists.", email));
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        UserDomain user = new UserDomain(request.name(), email, hashedPassword);

        repository.add(user);

    }


}
