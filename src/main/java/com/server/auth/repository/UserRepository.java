package com.server.auth.repository;


import com.server.auth.domain.UserDomain;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Optional;

@Repository
public class UserRepository {

    private final static String INSERT = "INSERT INTO authentication.user (name, email, password) VALUES (:name, :email, :password)";
    private final static String FIND_BY_EMAIL = "SELECT * FROM authentication.user where email = :email";

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void add(UserDomain user) {
        long affected = jdbcClient.sql(INSERT)
                .param("name", user.name())
                .param("email", user.email())
                .param("password", user.password())
                .update();

        Assert.isTrue(affected == 1, "Could not add user");
    }

    public Optional<UserDomain> findByEmail(String email) {
        return jdbcClient.sql(FIND_BY_EMAIL)
                .param("email", email)
                .query(UserDomain.class)
                .optional();
    }
}
