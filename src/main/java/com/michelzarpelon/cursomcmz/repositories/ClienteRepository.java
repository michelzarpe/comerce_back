package com.michelzarpelon.cursomcmz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.michelzarpelon.cursomcmz.domain.Cliente;

@Repository
public interface ClienteRepository
		extends JpaRepository<Cliente, Integer> {/* integer é o tipo do atributo identificador */

	/* readOnly, nao precisa ser envolvida com uma transacao de banco de dados */
	@Transactional(readOnly = true)
	Cliente findByEmail(String email);
	/*
	 * implementando o metodo dessa forma (usando o padrao de nomes do Spring) o
	 * Spring Data entende e faz a implementacao automatica
	 */

}
