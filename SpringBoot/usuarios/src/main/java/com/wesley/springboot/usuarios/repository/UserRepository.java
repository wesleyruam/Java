package com.wesley.springboot.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesley.springboot.usuarios.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long>{

}
