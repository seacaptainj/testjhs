package com.jeong2k.restapi.configs;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jeong2k.restapi.accounts.Account;
import com.jeong2k.restapi.accounts.AccountRole;
import com.jeong2k.restapi.accounts.AccountService;
import com.jeong2k.restapi.common.AppProperties;

@Configuration
public class AppConfig {
	
	@Autowired
	AppProperties appProperties;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}
	
	
	@Bean
	public ApplicationRunner applicationRunner() {
		
		return new ApplicationRunner() {
				@Autowired
				AccountService accountService;
	
				@Override
				public void run(ApplicationArguments args) throws Exception {
					Account user = Account.builder()
											.email(appProperties.getUserUsername())
											.password(appProperties.getUserPassword())
											.roles(Set.of(AccountRole.USER))
											.build();

					accountService.saveAccount(user);
					
					Account admin = Account.builder()
							.email(appProperties.getAdminUsername())
							.password(appProperties.getAdminPassword())
							.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
							.build();

					accountService.saveAccount(admin);
				}
		};
	}
}


