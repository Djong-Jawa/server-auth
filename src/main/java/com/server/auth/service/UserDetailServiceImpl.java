package com.server.auth.service;


import com.server.auth.domain.UserDomain;
import com.server.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        UserDomain user = repository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(String.format("User does not exist")));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.email())
                .password(user.password())
                .build();
    }
}
