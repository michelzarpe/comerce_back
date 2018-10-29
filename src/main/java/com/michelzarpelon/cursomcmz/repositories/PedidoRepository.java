package com.michelzarpelon.cursomcmz.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.michelzarpelon.cursomcmz.domain.Cliente;
import com.michelzarpelon.cursomcmz.domain.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {/*integer é o tipo do atributo identificador*/

	@Transactional(readOnly=true)
	Page<Pedido> findByCliente(Cliente cliente,Pageable pageRequest);
	
}
