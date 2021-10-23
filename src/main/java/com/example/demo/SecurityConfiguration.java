package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.RolesEnum;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests().antMatchers("/", "/login/**", "/cadastro/**", "../static/**", "/forgot_password/**", "/reset_password/**", "/message/**")
			.permitAll()
			.antMatchers("/atividades_aprovacao/**", "/novo_usuario/**", "/aprovarAtividades/**", "/adminCadastro/**", "/logAdmin/**", "/informacoesUsuario/**").hasAnyAuthority(RolesEnum.ADMIN.name())
			.anyRequest().authenticated()
			.and()
			.exceptionHandling().accessDeniedPage("/403")
			.and()
			.formLogin(form -> form
					.loginPage("/login")
					.failureUrl("/login-error")
					.usernameParameter("email")
					.passwordParameter("senha")
					.defaultSuccessUrl("/dashboard", true)
					.permitAll()
			)
			.logout(logout -> {
				logout.logoutUrl("/logout")
					.logoutSuccessUrl("/");
			});
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/vendor/**");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		auth.authenticationProvider(authenticationProvider());
	}
	
}
