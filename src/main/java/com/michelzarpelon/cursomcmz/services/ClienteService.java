package com.michelzarpelon.cursomcmz.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.michelzarpelon.cursomcmz.domain.Cidade;
import com.michelzarpelon.cursomcmz.domain.Cliente;
import com.michelzarpelon.cursomcmz.domain.Endereco;
import com.michelzarpelon.cursomcmz.domain.dto.ClienteDTO;
import com.michelzarpelon.cursomcmz.domain.dto.NovoClienteDTO;
import com.michelzarpelon.cursomcmz.domain.enums.Perfil;
import com.michelzarpelon.cursomcmz.domain.enums.TipoCliente;
import com.michelzarpelon.cursomcmz.repositories.CidadeRepository;
import com.michelzarpelon.cursomcmz.repositories.ClienteRepository;
import com.michelzarpelon.cursomcmz.repositories.EnderecoRepository;
import com.michelzarpelon.cursomcmz.security.UserSS;
import com.michelzarpelon.cursomcmz.services.execeptions.AuthorizationException;
import com.michelzarpelon.cursomcmz.services.execeptions.DataIntegrityException;
import com.michelzarpelon.cursomcmz.services.execeptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repositorioObj;

	@Autowired
	private CidadeRepository repositorioObjCidade;

	@Autowired
	private EnderecoRepository repositorioObjEndereco;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private ImageService imageService;

	@Value("${img.prefix.client.profile}")
	private String prefixo;

	@Value("${img.profile.size}")
	private Integer size;

	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		// Se user for nulo ou nao tiver perfil de admin e nao for usuario pesquisado
		// lança uma excessao
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado!!!");
		}

		Cliente cliente = repositorioObj.findOne(id);
		if (cliente == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado: " + id + ", Tipo do objeto: " + Cliente.class.getName());
		}
		return cliente;
	}

	public Cliente update(Cliente obj) {
		Cliente objBanco = find(obj.getId());
		obj.setCpfOuCnpj(objBanco.getCpfOuCnpj());
		obj.setTipo(objBanco.getTipo());
		return repositorioObj.save(obj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repositorioObj.delete(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Objeto não pode ser deletado: " + id + ", Tipo do objeto: "
					+ Cliente.class.getName() + ", pois possui Entidades Relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return repositorioObj.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repositorioObj.findAll(pageRequest);
	}

	/*
	 * garante que vai salvar cliente e endereco na mesma transacao no banco de
	 * dados
	 */
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repositorioObj.save(obj);
		repositorioObjEndereco.save(obj.getEnderecos());
		return obj;

	}

	/*---------------------------upload foto perfil---------------------------------------*/

	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}

		BufferedImage jpgimage = imageService.getJpgImageFromFile(multipartFile);
		jpgimage = imageService.cropSquare(jpgimage);
		jpgimage = imageService.resize(jpgimage, size);
		String fileName = prefixo + user.getId() + ".jpg";
		return s3Service.uploadFile(imageService.getInputStream(jpgimage, "jpg"), fileName, "image");
	}

	/*---------------------------Sobre Carga dos DTOs---------------------------------------*/
	/* converter ObrjetoDTO para objeto normal */
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}

	/* Usado o DTO para transferir objetos entre front e back and */
	public Cliente fromDTO(NovoClienteDTO objDTO) {
		Cliente objCli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(),
				TipoCliente.toEnum(objDTO.getTipo()), bCryptPasswordEncoder.encode(objDTO.getSenha()));
		Cidade objCid = repositorioObjCidade.findOne(objDTO.getCidadeId());
		Endereco objEnd = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(),
				objDTO.getBairro(), objDTO.getCep(), objCli, objCid);
		objCli.getEnderecos().add(objEnd);

		if (objDTO.getTelefone1() != null)
			objCli.getTelefone().add(objDTO.getTelefone1());

		if (objDTO.getTelefone2() != null)
			objCli.getTelefone().add(objDTO.getTelefone2());

		if (objDTO.getTelefone3() != null)
			objCli.getTelefone().add(objDTO.getTelefone3());

		return objCli;
	}

	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticated();
		// Se user for nulo ou nao tiver perfil de admin e nao for usuario pesquisado
		// lança uma excessao
		if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado!!!");
		}

		Cliente obj = repositorioObj.findByEmail(email);
		if (obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado: " + Cliente.class.getName());
		}
		return obj;
	}

}
