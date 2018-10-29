package com.michelzarpelon.cursomcmz.services.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.michelzarpelon.cursomcmz.domain.Cliente;
import com.michelzarpelon.cursomcmz.domain.dto.NovoClienteDTO;
import com.michelzarpelon.cursomcmz.domain.enums.TipoCliente;
import com.michelzarpelon.cursomcmz.repositories.ClienteRepository;
import com.michelzarpelon.cursomcmz.resources.exceptions.FieldMessage;
import com.michelzarpelon.cursomcmz.services.validation.util.Br;

/*Adiciona as regras de negocio para inserção de um cliente na base de dados*/
public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, NovoClienteDTO> {

	@Autowired
	private ClienteRepository repositorioObj;

	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(NovoClienteDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<>();

		Cliente objAux = repositorioObj.findByEmail(objDto.getEmail());
		
		if (objAux != null) {
			list.add(new FieldMessage("Email", "E-mail já existente"));
		}

		if (objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !Br.isValidCPF(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("CpfOuCnpj", "CPF Inválido"));

		}

		if (objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !Br.isValidCNPJ(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("CpfOuCnpj", "CNPJ Inválido"));

		}

		//transporta os erros personalizados para o framework.. seria como eu tivesse criando minhas exceções
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessege()).addPropertyNode(e.getFieldName()).addConstraintViolation();
		}
		return list.isEmpty();
	}
}