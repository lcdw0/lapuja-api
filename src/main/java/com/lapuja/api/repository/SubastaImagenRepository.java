package com.lapuja.api.repository;

import com.lapuja.api.entity.SubastaImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubastaImagenRepository extends JpaRepository<SubastaImagen, Long> {

    List<SubastaImagen> findBySubastaIdOrderByOrdenAsc(Long subastaId);

    long countBySubastaId(Long subastaId);

    void deleteBySubastaId(Long subastaId);

    List<SubastaImagen> findBySubastaId(Long subastaId);

    SubastaImagen findBySubastaIdAndPrincipalTrue(Long subastaId);
}