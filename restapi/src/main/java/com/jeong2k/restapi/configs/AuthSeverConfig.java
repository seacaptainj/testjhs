package com.jeong2k.restapi.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.jeong2k.restapi.accounts.AccountService;
import com.jeong2k.restapi.common.AppProperties;

@Configuration
@EnableAuthorizationServer
public class AuthSeverConfig extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	TokenStore tokenStore;
	
	@Autowired
	AppProperties appProperties;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// TODO Auto-generated method stub
//		super.configure(security);
		security.passwordEncoder(passwordEncoder);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// TODO Auto-generated method stub
		endpoints.authenticationManager(authenticationManager)
				 .userDetailsService(accountService)
				 .tokenStore(tokenStore);
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// TODO Auto-generated method stub
		clients.inMemory().withClient(appProperties.getClientId()) //clientId
				.authorizedGrantTypes("password", "refresh_token") //Grant유형
				.scopes("read", "write") //읽기, 쓰기
				.secret(this.passwordEncoder.encode(appProperties.getClientSecret())) //secretId
				.accessTokenValiditySeconds(10 * 60) //token 유효 시간 10분
				.refreshTokenValiditySeconds(6 * 10 * 60); //refresh token 유효 시간 1시간 
	}

}
