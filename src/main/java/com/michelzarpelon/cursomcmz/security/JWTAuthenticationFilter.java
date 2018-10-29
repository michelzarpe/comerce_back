package com.michelzarpelon.cursomcmz.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelzarpelon.cursomcmz.domain.dto.CredencialDTO;

/*UsernamePasswordAuthenticationFilter o spring security vai saber que tem que interceptar a requesicao do endPoint /login  */

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager autenticatiomManager;

	private JWTUtil jwtUtil;

	public JWTAuthenticationFilter(AuthenticationManager autenticatiomManager, JWTUtil jwtUtil) {
		this.autenticatiomManager = autenticatiomManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			CredencialDTO creds = new ObjectMapper().readValue(request.getInputStream(), CredencialDTO.class);
			
			UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha(),new ArrayList<>());
			
			Authentication auth = autenticatiomManager.authenticate(authenticationToken);/*authenticate, metodo verifica se os usuarios sao validos
			confome a implementacao no UserDetail e User DetailService*/
			return auth;/*esse objeto auth, informa para o spring security se a autenticacao deu certo ou nao*/
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/* Se a autenticacao ocorrer com sucesso o que tem que fazer.. */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String userName = ((UserSS) authResult.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(userName);
		response.addHeader("Authorization", "Bearer "+token);
		response.addHeader("access-control-expose-headers", "Authorization");
	}

}
