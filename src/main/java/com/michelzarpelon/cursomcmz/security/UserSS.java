package com.michelzarpelon.cursomcmz.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.michelzarpelon.cursomcmz.domain.enums.Perfil;

public class UserSS implements UserDetails{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String email;
	private String senha;
	private Collection<? extends GrantedAuthority> authorities;

	public UserSS() {
		super();
	}
	public UserSS(Integer id, String email, String senha, Set<Perfil> perfils) {
		super();
		this.id = id;
		this.email = email;
		this.senha = senha;
		this.authorities = perfils.stream().map(x -> new SimpleGrantedAuthority(x.getDescricao())).collect(Collectors.toList());
	}



	public Integer getId() {
		return id;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	/*regra de negocio que a conta expira ou nao, pode implementar*/
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/*regra de negocio que a conta bloquiada ou nao, pode implementar*/
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	/*regra de negocio que as credenciais estao expirada, pode implementar*/
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	/*regra de negocio que a conta esta bloquiada, pode implementar*/
	@Override
	public boolean isEnabled() {
		return true;
	}
	// testa se o usuario comtem no perfil a autorizacao, mais especifico para ADMIM
	public boolean hasRole(Perfil perfil) {
		return getAuthorities().contains(new SimpleGrantedAuthority(perfil.getDescricao()));
	}

}
