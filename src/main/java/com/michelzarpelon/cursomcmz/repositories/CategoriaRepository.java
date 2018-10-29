package com.michelzarpelon.cursomcmz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.michelzarpelon.cursomcmz.domain.Categoria;


/*
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
*/
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {/*integer é o tipo do atributo identificador*/

}
