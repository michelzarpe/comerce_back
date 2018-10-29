package com.michelzarpelon.cursomcmz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.michelzarpelon.cursomcmz.domain.Categoria;
import com.michelzarpelon.cursomcmz.domain.dto.CategoriaDTO;
import com.michelzarpelon.cursomcmz.repositories.CategoriaRepository;
import com.michelzarpelon.cursomcmz.services.execeptions.DataIntegrityException;
import com.michelzarpelon.cursomcmz.services.execeptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repositorioObj;

	public Categoria find(Integer id) {
		Categoria categoria = repositorioObj.findOne(id);
		if (categoria == null) {
			throw new ObjectNotFoundException(
					"Objeto não encontrado: " + id + ", Tipo do objeto: " + Categoria.class.getName());
		}
		return categoria;
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repositorioObj.save(obj);
	}

	/*buscar no banco sempre porque ele fica monitorado pelo JPA*/
	public Categoria update(Categoria obj) {
		Categoria objBanco = find(obj.getId());
		updateData(objBanco, obj);
		return repositorioObj.save(objBanco);
	}

	private void updateData(Categoria objBanco, Categoria obj) {
		objBanco.setNome(obj.getNome());

	}

	public void delete(Integer id) {
		find(id);
		try {
			repositorioObj.delete(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Objeto não pode ser deletado: " + id + ", Tipo do objeto: "+ Categoria.class.getName() + ", pois possui produtos");
		}
	}

	public List<Categoria> findAll() {
		return repositorioObj.findAll();
	}

	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repositorioObj.findAll(pageRequest);
	}

	/* converter ObrjetoDTO para objeto normal */
	public Categoria fromDTO(CategoriaDTO objDTO) {
		return new Categoria(objDTO.getId(), objDTO.getNome());
	}

}
