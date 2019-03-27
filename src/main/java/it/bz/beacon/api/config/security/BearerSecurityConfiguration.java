package it.bz.beacon.api.config.security;

import it.bz.beacon.api.security.JwtConfigurer;
import it.bz.beacon.api.security.filter.JwtExceptionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@Order(2)
public class BearerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfigurer jwtConfigurer;

    @Autowired
    private JwtExceptionFilter jwtExceptionFilter;

    @Value("${security.jwt.token.expire-length}")
    private long tokenExpireLength;

    @Value("${security.jwt.token.secret}")
    private String jwtSecret;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/v1/admin/**").authenticated()
                .and()
                .apply(jwtConfigurer);
    }

    public long getTokenExpireLength() {
        return tokenExpireLength;
    }

    public byte[] getJwtSecret() {
        return jwtSecret.getBytes();
    }
}