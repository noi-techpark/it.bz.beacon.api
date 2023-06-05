// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.config.security;

import it.bz.beacon.api.security.JwtConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class BearerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfigurer jwtConfigurer;

    @Value("${security.jwt.token.expire-length}")
    private long tokenExpireLength;

    @Value("${security.jwt.token.password-reset-expire-length}")
    private long passwordResetTokenExpireLength;

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

    public long getPasswordResetTokenExpireLength() {
        return passwordResetTokenExpireLength;
    }

    public byte[] getJwtSecret() {
        return jwtSecret.getBytes();
    }
}