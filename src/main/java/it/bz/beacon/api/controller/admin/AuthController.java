package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.auth.InvalidJwtAuthenticationException;
import it.bz.beacon.api.exception.auth.InvalidJwtPasswordResetToken;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        String token = request.getToken();
        try {
            valid = jwtTokenProvider.validateToken(request.getToken());
        } catch (InvalidJwtAuthenticationException e) {
            valid = false;
        }
        return new AuthenticationTokenCheck(token, valid);
    }

    @ApiOperation(value = "Check whether a token is valid or not")
    @RequestMapping(method = RequestMethod.POST, value = "/resetPassword/request", produces = "application/json")
    public BaseMessage resetPasswordRequest(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String username = resetPasswordRequest.getUsername();

        String token = jwtTokenProvider.createPasswordResetToken(this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")));
        //TODO send mail with token

        System.out.println(token);

        return new BaseMessage("Password request sent");
    }

    @ApiOperation(value = "Check whether a token is valid or not")
    @RequestMapping(method = RequestMethod.POST, value = "/resetPassword/change", produces = "application/json")
    public BaseMessage resetPasswordChange(@Valid @RequestBody ResetPasswordChange resetPasswordChange) {
        String token = resetPasswordChange.getToken();
        try {
            boolean valid = jwtTokenProvider.validateToken(token);
            if (!valid)
                throw new InvalidJwtPasswordResetToken();

        } catch (InvalidJwtAuthenticationException e) {
            throw new InvalidJwtPasswordResetToken();
        }
        String username = jwtTokenProvider.getUsername(token);
        User user = this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));

        Long updatedAtToken = jwtTokenProvider.getBody(token).get("updatedAt", Long.class);
        if (updatedAtToken != user.getUpdatedAt().getTime())
            throw new InvalidJwtPasswordResetToken();

        user.setPassword(passwordEncoder.encode(resetPasswordChange.getNewPassword()));
        users.save(user);

        return new BaseMessage("Password changed");
    }
}
