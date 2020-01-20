package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.auth.InvalidJwtAuthenticationException;
import it.bz.beacon.api.exception.auth.InvalidJwtPasswordResetToken;
import it.bz.beacon.api.exception.email.EmailNotSentException;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
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

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

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

        User user = this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));

        String token = jwtTokenProvider.createPasswordResetToken(user);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject("Reset password instructions");
            helper.setFrom(beaconSuedtirolConfiguration.getPasswordResetEmailFrom());
            helper.setTo(user.getEmail());
            helper.setText(String.format(
                    "Hello %s! <br/><br/>" +
                            "Someone has requested a link to change your password, and you can do this through the link below. <br/><br/>" +
                            "<a href=\"%s%s%s\">Change your password</a> <br/><br/>" +
                            "If you didn't request this, please ignore this email. <br/><br/>" +
                            "Your password won't change until you access the link above and create a new one.",
                    user.getName(),
                    beaconSuedtirolConfiguration.getPasswordResetURL(),
                    "/#/reset-password-change/",
                    token
            ), true);
            emailSender.send(message);
            return new BaseMessage("Password request sent");
        } catch (Exception e) {
            throw new EmailNotSentException();
        }

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
