package com.wesleyruan.encurtador.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wesleyruan.encurtador.model.UrlModel;

public interface UrlRepository extends JpaRepository<UrlModel, Long>{
    boolean existsByCodeUrl(String codeUrl);
    Optional<UrlModel> findByCodeUrl(String codeUrl);
}
