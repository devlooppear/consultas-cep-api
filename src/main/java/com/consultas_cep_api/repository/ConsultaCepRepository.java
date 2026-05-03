package com.consultas_cep_api.repository;

import com.consultas_cep_api.domain.entity.ConsultaCep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaCepRepository extends JpaRepository<ConsultaCep, Long> {}
