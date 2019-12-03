package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.auth.InvalidJwtAuthenticationException;
import it.bz.beacon.api.model.AuthenticationRequest;
import it.bz.beacon.api.model.AuthenticationToken;
import it.bz.beacon.api.model.AuthenticationTokenCheck;
import it.bz.beacon.api.model.AuthenticationTokenCheckRequest;
import it.bz.beacon.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository users;

    @PostMapping("/signin")
    public AuthenticationToken signin(@Valid @RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")));
            return new AuthenticationToken(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @ApiOperation(value = "Check whether a token is valid or not")
    @RequestMapping(method = RequestMethod.POST, value = "/checkToken", produces = "application/json")
    public AuthenticationTokenCheck checkToken(@Valid @RequestBody AuthenticationTokenCheckRequest request) {
        boolean valid;
        try {
            valid = jwtTokenProvider.validateToken(request.getToken());
        } catch (InvalidJwtAuthenticationException e) {
            valid = false;
        }
        return new AuthenticationTokenCheck(request.getToken(), valid);
    }
}
