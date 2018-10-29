package com.michelzarpelon.cursomcmz.services;

import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.michelzarpelon.cursomcmz.domain.Cliente;
import com.michelzarpelon.cursomcmz.domain.ItemPedido;
import com.michelzarpelon.cursomcmz.domain.PatamentoComBoleto;
import com.michelzarpelon.cursomcmz.domain.Pedido;
import com.michelzarpelon.cursomcmz.domain.enums.EstadoPagamento;
import com.michelzarpelon.cursomcmz.repositories.ItemPedidoRepository;
import com.michelzarpelon.cursomcmz.repositories.PagamentoRepository;
import com.michelzarpelon.cursomcmz.repositories.PedidoRepository;
import com.michelzarpelon.cursomcmz.security.UserSS;
import com.michelzarpelon.cursomcmz.services.execeptions.AuthorizationException;
import com.michelzarpelon.cursomcmz.services.execeptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repositorioObj;
	
	@Autowired
	private PagamentoRepository repositorioPag;
	
	@Autowired
	private ItemPedidoRepository repositorioItemPedido;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private EmailService emailService;
	
	
	public Pedido find(Integer id) {
		Pedido pedido = repositorioObj.findOne(id);
		if(pedido==null) {
			throw new ObjectNotFoundException("Objeto não encontrado: "+id+", Tipo do objeto: "+Pedido.class.getName());
		}
		return pedido;
	}

	@Transactional
	public Pedido insert(Pedido _obj) {
		_obj.setId(null);
		_obj.setInstante(new Date());
		_obj.setCliente(clienteService.find(_obj.getCliente().getId()));
		_obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		_obj.getPagamento().setPedido(_obj);
		
		//suposicao pra calcular as datas
		if (_obj.getPagamento() instanceof PatamentoComBoleto) {
			PatamentoComBoleto pagamento = (PatamentoComBoleto) _obj.getPagamento();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(_obj.getInstante());
			cal.add(Calendar.DAY_OF_MONTH, 7);
			pagamento.setDataVencimento(cal.getTime());
		}
		
		_obj = repositorioObj.save(_obj);
		repositorioPag.save(_obj.getPagamento());
		//pegando os precos do produtos que estao nos itens de pedido do pedido
		for(ItemPedido ip: _obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(_obj);
		}
		repositorioItemPedido.save(_obj.getItens());
		//emailService.sendOrderConfirmationEmail(_obj);
		 emailService.sendOrderConfirmationHtmlEmail(_obj);
		return _obj;
	}
	
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if(user==null) {
			throw new AuthorizationException("Acesso negado!!!");
		}
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.find(user.getId());
		
		return repositorioObj.findByCliente(cliente, pageRequest);
	}
	
}
