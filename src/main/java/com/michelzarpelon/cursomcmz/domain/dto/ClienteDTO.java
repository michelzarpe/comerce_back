package com.michelzarpelon.cursomcmz.domain.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.michelzarpelon.cursomcmz.domain.Cliente;
import com.michelzarpelon.cursomcmz.services.validation.ClienteUpdate;


@ClienteUpdate
public class ClienteDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotEmpty(message="Favor preencher o campo!")
	@Length(max=100, min=10, message="Favor Preencher campo com no 5 a 80 ctrs!")
	private String nome;
	
	@NotEmpty(message="Favor preencher o campo!")
	@Email(message="E-mail inválido")
	private String email;
	
	public ClienteDTO() {

	}

	public ClienteDTO(Cliente obj) {
		super();
		this.id = obj.getId();
		this.nome = obj.getNome();
		this.email = obj.getEmail();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
