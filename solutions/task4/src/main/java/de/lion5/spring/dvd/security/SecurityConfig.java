package de.lion5.spring.dvd.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder createEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);

        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.createEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/users", "/h2-console/**").hasRole("ADMIN")
            .antMatchers("/movies").hasAnyRole("ADMIN", "USER")
            .antMatchers("/", "/**").permitAll()
            .and()
            .formLogin().loginPage("/login")
            .and()
            .logout().logoutSuccessUrl("/")
            .and().csrf().ignoringAntMatchers("/h2-console/**") // needed to access the h2-console after introducing security module;
            .and().csrf().ignoringAntMatchers("/v1/**")
            .and().headers().frameOptions().sameOrigin() // needed to access the h2-console after introducing security module
            .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login");
    }

    /**
     * Only for really simple cases.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login");
    }
}

