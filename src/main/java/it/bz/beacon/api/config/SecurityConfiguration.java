package it.bz.beacon.api.config;

import it.bz.beacon.api.security.JwtConfigurer;
import it.bz.beacon.api.security.filter.JwtExceptionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

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
                .antMatchers("/v1/admin/beacons/**").authenticated()
                .and()
                .apply(jwtConfigurer);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public long getTokenExpireLength() {
        return tokenExpireLength;
    }

    public byte[] getJwtSecret() {
        return jwtSecret.getBytes();
    }
}