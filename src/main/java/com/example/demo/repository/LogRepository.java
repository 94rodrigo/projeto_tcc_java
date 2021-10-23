package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LogDeAcoes;

@Repository("logRepository")
public interface LogRepository extends JpaRepository<LogDeAcoes, Long>{

}
