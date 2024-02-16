package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableMethodSecurity
public class SecurityConfig{
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}
 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
  //We need bean of DaoAuthenticationProvider because we will be our providing security for dao(memory/db) objects 
  	@Bean
  	public DaoAuthenticationProvider authenticationProvider() {
  		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
  		daoAuthenticationProvider.setUserDetailsService(this.userDetailsService());
  		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
  		return daoAuthenticationProvider;
  	}
  	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     
        http.csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/admin/**")
            .hasRole("ADMIN")
            .requestMatchers("/user/**")
            .hasRole("USER")
            .requestMatchers("/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and().formLogin()
            .loginPage("/signin")
            .defaultSuccessUrl("/user/index")
            .loginProcessingUrl("/dologin");
        return http.build();
    }
   
	
}
