package com.lapuja.api.repository;

import com.lapuja.api.entity.Puja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PujaRepository extends JpaRepository<Puja, Long> {

    List<Puja> findByUsuarioId(Long usuarioId);

    List<Puja> findBySubastaId(Long subastaId);
}