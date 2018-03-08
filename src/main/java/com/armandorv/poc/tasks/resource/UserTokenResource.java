package com.armandorv.poc.tasks.resource;

import javax.validation.Valid;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.armandorv.poc.tasks.domain.User;
import com.armandorv.poc.tasks.resource.dto.UserTokenDTO;
import com.armandorv.poc.tasks.security.jwt.JWTAuthenticationProvider;

import reactor.core.publisher.Mono;

@RestController
public class UserTokenResource {
	
	private static final String AUTH_HEADER_NAME = "Authorization";
	private static final String AUTH_HEADER_VALUE = "Bearer %s";

	private ReactiveAuthenticationManager authenticationManager;

	private JWTAuthenticationProvider tokenProvider; 
	
	public UserTokenResource(ReactiveAuthenticationManager authenticationManager, JWTAuthenticationProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}
	
    @PostMapping("/authenticate")
    public Mono<UserTokenDTO> authenticate(ServerWebExchange exchange, @Valid @RequestBody User user) {

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        return authenticationManager
        		.authenticate(authentication)
        		.doOnNext(ReactiveSecurityContextHolder::withAuthentication)
        		.map(this::toUserToken)
        		.doOnSuccess(userToken -> addTokenAsHeader(exchange, userToken));
    }
    
    private UserTokenDTO toUserToken(Authentication authentication) {
    	final String token = tokenProvider.toToken(authentication);
    	return new UserTokenDTO("User authenticated sucessfully.", token);
    }
    
    private void addTokenAsHeader(ServerWebExchange exchange, UserTokenDTO userToken) {
    	exchange.getResponse().getHeaders()
			.add(AUTH_HEADER_NAME, String.format(AUTH_HEADER_VALUE, userToken.getToken()));
    }
 
}