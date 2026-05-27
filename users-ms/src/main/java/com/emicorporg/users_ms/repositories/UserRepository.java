package com.emicorporg.users_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emicorporg.users_ms.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
    UserEntity findByEmail(String email);
}
