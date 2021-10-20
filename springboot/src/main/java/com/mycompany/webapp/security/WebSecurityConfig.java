package com.mycompany.webapp.security;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("Run HttpSecurity http");
		
		//로그인 설정
		http.formLogin()
			.loginPage("/security/loginForm") 		//default: /login (GET)
			.loginProcessingUrl("/login")	  		//default: /login (POST)
			.failureUrl("/security/loginError")		//default: /login?error
			.defaultSuccessUrl("/")
			.usernameParameter("mid")				//default: username
			.passwordParameter("mpassword");		//default: password
		
		//로그아웃 설정
		http.logout()
			.logoutUrl("/logout")					//defualt: /logout
			.logoutSuccessUrl("/");
		
		//URL 권한 설정
		http.authorizeRequests()
			.antMatchers("/security/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/security/manager/**").hasAuthority("ROLE_MANAGER")
			.antMatchers("/security/user/**").authenticated() //로그인된(인증된) 모든 사용자 접근 가능
			.antMatchers("/**").permitAll();
		
		//권한 없음(403)일 경우 이동할 경로 설정
		http.exceptionHandling().accessDeniedPage("/security/accessDenied");
		
		//CSRF(사이트 요청 위조 방지) 비활성화
		http.csrf().disable();
	}
	
	@Resource
	private DataSource dataSource;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		log.info("Run passwordEncoder()");
		
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("Run AuthenticationManagerBuilder");
		
		//DB에서 가져올 사용자 정보 설정
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery("SELECT mid, mpassword, menabled FROM member WHERE mid=?")
			.authoritiesByUsernameQuery("SELECT mid, mrole FROM member WHERE mid=?")
			//패스워드 인코딩 방법 설정
			.passwordEncoder(passwordEncoder()); //default: DelegatingPasswordEncoder
	}
	
	//권한 계층을 참조하기 위해 HttpSecurity에서 사용하기 때문에 관리빈으로 반드시 등록해서 사용해야 함
	@Bean
	public RoleHierarchyImpl roleHierarchyImpl() {
		log.info("Run roleHierarchyImpl()");
		
		RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();
		roleHierarchyImpl.setHierarchy("ROLE_ADMIN > ROLE_MANAGER > ROLE_USER");
		
		return roleHierarchyImpl;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		log.info("Run WebSecurity web");
		
		//권한 계층 관계 설정
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchyImpl());
		web.expressionHandler(handler);
		
		//인증 절차가 필요 없는 경로 설정
		web.ignoring()
		   .antMatchers("/bootstrap-4.6.0-dist/**")
		   .antMatchers("/css/**")
		   .antMatchers("/images/**")
		   .antMatchers("/jquery/**")
		   .antMatchers("/favicon.ico");
	}
	
}
