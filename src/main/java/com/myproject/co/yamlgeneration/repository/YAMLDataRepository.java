package com.myproject.co.yamlgeneration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.co.yamlgeneration.model.YAMLData;

public interface YAMLDataRepository extends JpaRepository<YAMLData, Long> {
}