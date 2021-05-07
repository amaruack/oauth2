package com.eseict.sso.server;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.eseict.sso.server.provider.CustomAuthenticationProvider;
import com.eseict.sso.server.security.CustomAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//
		http
			.authorizeRequests()
				.antMatchers("/home", "/webjars/**", "/css/**", "/img/**", "/component/**", "/js/**", "/common/**","/userInfo").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginProcessingUrl("/login")
				.loginPage("/loginForm")
				.permitAll()
//				.failureHandler(customAuthenticationFailureHandler())
				.and()
			.csrf()
				.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/user*"))
				.disable()
			.logout()
				.permitAll();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(customAuthenticationProvider);
//		auth
//			.inMemoryAuthentication()
//			.withUser("tsong").password("aaa").roles("USER").and()
//			.withUser("jmpark").password("aaa").roles("USER").and()
//			.withUser("jkkang").password("aaa").roles("USER").and()
//			.withUser("test").password("aaa").roles("USER","ADMIN");
//		JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> config = auth.jdbcAuthentication();
//		auth.jdbcAuthentication().dataSource(dataSource);
//		auth.userDetailsService(userDetailsService);
	}
 
    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
	
}
