package com.example.Blogbackend.user;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    public final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs,rowNum)->new User(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("display_name"),
            rs.getString("role"),
            rs.getTimestamp("created_at").toInstant()
    );

    public Optional<User> findByEmail(String email){
        return jdbc.query("SELECT * FROM users WHERE email=?",USER_ROW_MAPPER,email).stream().findFirst();
    }
    public long insert(String email,String passwordHash,String displayName,String role){
        try {
            jdbc.update("INSERT INTO users(email, password_hash, display_name, role) VALUES (?,?,?,?)",
            email,passwordHash,displayName,role
            );

        }catch (DuplicateKeyException e){
            throw e;
        }
        return jdbc.queryForObject("SELECT LAST_INSERT_()",Long.class);
    }
}
