package com.michelzarpelon.cursomcmz.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.michelzarpelon.cursomcmz.services.DBService;
import com.michelzarpelon.cursomcmz.services.EmailService;
import com.michelzarpelon.cursomcmz.services.MocEmailService;

@Configuration
@Profile("teste")
public class TesteConfig {

	@Autowired
	private DBService dbservice;  
	
	@Bean
	public boolean instantiateDatabase() throws ParseException {
		dbservice.instantiateTesteDatabase();
		return true;
	}
	
	
	@Bean //quando tem um metodo com anotacao bean, ele vira um componente no sistema Entao se em outra classe tu fizer uma insntancia de EmailService ele procura pelo bean e faz a regra 
	public EmailService emailService() {
		return new MocEmailService();
	}
	
}
