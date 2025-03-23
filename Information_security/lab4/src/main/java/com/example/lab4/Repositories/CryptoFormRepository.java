package com.example.lab4.Repositories;

import com.example.lab4.Models.CryptoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoFormRepository extends JpaRepository<CryptoModel, Long> {
}