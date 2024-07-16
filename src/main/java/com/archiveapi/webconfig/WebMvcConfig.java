package com.archiveapi.webconfig;
//package com.archiveapi.webconfig;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//i
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
//public class WebMvcConfig extends WebSecurityConfigurerAdapter {
//
//	// private final UserDetailsServiceImpl userDetailsService;
//
//	private final JwtAuthenticationEntryPoint jwtEntryPoint;
//
//	private final InspectorDetailServiceImpl insepctorDetailsService;
//
//	@Bean
//	public JwtAuthenticationFilter authenticationJwtTokenFilter() {
//		return new JwtAuthenticationFilter();
//	}
//
//	@Autowired
//	public WebMvcConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationEntryPoint jwtEntryPoint,
//			InspectorDetailServiceImpl insepctorDetailsService) {
//		// this.userDetailsService = userDetailsService;
//		this.jwtEntryPoint = jwtEntryPoint;
//		this.insepctorDetailsService = insepctorDetailsService;
//	}
//
//	@Override
//	@Bean
//	public AuthenticationManager authenticationManager() throws Exception {
//		return super.authenticationManager();
//	}
//
//	@Bean
//	public JwtAuthenticationFilter jwtAuthenticationFilter() {
//		return new JwtAuthenticationFilter();
//	}
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(userAuthProvider());// .authenticationProvider(adminAuthProvider());
//	}
//
//	@Bean
//	public AuthenticationProvider userAuthProvider() {
//		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//		provider.setUserDetailsService(insepctorDetailsService);
//		provider.setPasswordEncoder(passwordEncoder());
//		return provider;
//	}
//
//	/*@Bean
//	public AuthenticationProvider adminAuthProvider() {
//		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//		// provider.setUserDetailsService(userDetailsService);
//		provider.setPasswordEncoder(passwordEncoder());
//		return provider;
//	*/
//
//	@Override
//	public void configure(WebSecurity web) {
//		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**",
//				"/swagger-ui.html", "/webjars/**");
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(jwtEntryPoint).and()
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
//				.antMatchers("/", "/favicon.ico", "/**/*.json", "/**/*.xml", "/**/*.properties", "/**/*.woff2",
//						"/**/*.woff", "/**/*.ttf", "/**/*.ttc", "/**/*.ico", "/**/*.bmp", "/**/*.png", "/**/*.gif",
//						"/**/*.svg", "/**/*.jpg", "/**/*.jpeg", "/**/*.html", "/**/*.css", "/**/*.js")
//				.permitAll().antMatchers("/**/api/auth/**").permitAll().antMatchers("/**/api/session/**").permitAll()
//				.antMatchers("/swagger-ui/**").permitAll().anyRequest().authenticated();
//
//		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//	}
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//}
//
