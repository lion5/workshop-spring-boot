package de.lion5.spring.dvd.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder createEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc, MvcRequestMatcher.Builder mvcMatcher, HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {


        http
                .authorizeRequests((requests) -> requests
                        .requestMatchers(mvc.pattern("/users"), mvcMatcher.servletPath("/h2-console").pattern("/**")).hasRole("ADMIN")
                        .requestMatchers(mvc.pattern("/movies")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(mvc.pattern("/"), mvc.pattern("/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                //.csrf((request) -> request.ignoringRequestMatchers(mvc.pattern("/h2-console/**")))
                .csrf(request -> request.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(option -> option.sameOrigin()))
                .logout((logout) -> logout.permitAll().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login");
    }


}
/*
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

        auth.userDetailsService(userDetailsService).passwordEncoder(createEncoder());
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
            .and().headers().frameOptions().sameOrigin() // needed to access the h2-console after introducing security module
            .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login");
    }
}
*/
