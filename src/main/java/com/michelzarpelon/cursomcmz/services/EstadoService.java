package com.michelzarpelon.cursomcmz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.michelzarpelon.cursomcmz.domain.Estado;
import com.michelzarpelon.cursomcmz.repositories.EstadoRepository;

@Service
public class EstadoService {

	
	
	@Autowired	
	private EstadoRepository estadoRepository;
	
	public List<Estado> findAll(){
		return estadoRepository.findAllByOrderByNome();
	}
	
	
}
