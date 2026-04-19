package com.mov.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mov.transport.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}