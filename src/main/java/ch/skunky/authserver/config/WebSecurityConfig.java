package ch.skunky.authserver.config;

import ch.skunky.authserver.service.SkunkUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * https://www.baeldung.com/spring-security-registration
 * https://www.baeldung.com/spring-security-authentication-with-a-database
 */
@Configuration
@EnableResourceServer
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SkunkUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


    /*
    @Autowired
    public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
	auth.inMemoryAuthentication()
	  .withUser("john").password(passwordEncoder.encode("123")).roles("USER").and()
	  .withUser("tom").password(passwordEncoder.encode("111")).roles("ADMIN").and()
	  .withUser("user1").password(passwordEncoder.encode("pass")).roles("USER").and()
	  .withUser("admin").password(passwordEncoder.encode("nimda")).roles("ADMIN");
    }// @formatter:on

    */

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
		http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/user/register").permitAll()
		        .antMatchers("/oauth/token/revokeById/**").permitAll()
		        .antMatchers("/tokens/**").permitAll()
		        .anyRequest().authenticated()
		        .and().formLogin().permitAll()
		        .and().csrf().disable();
		// @formatter:on
    }

}
