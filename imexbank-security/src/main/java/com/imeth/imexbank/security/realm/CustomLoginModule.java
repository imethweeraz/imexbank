package com.imeth.imexbank.security.realm;

import com.imeth.imexbank.entities.User;
import com.imeth.imexbank.security.utils.PasswordEncoder;
import com.imeth.imexbank.services.interfaces.SecurityService;
import jakarta.ejb.EJB;
import jakarta.security.auth.login.LoginException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;

public class CustomLoginModule implements LoginModule {

    @EJB
    private SecurityService securityService;

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    private String username;
    private User user;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, 
                          Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("Username: ");
        PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (Exception e) {
            throw new LoginException("Failed to get credentials");
        }

        username = nameCallback.getName();
        char[] password = passwordCallback.getPassword();

        if (username == null || password == null) {
            throw new LoginException("Username or password cannot be null");
        }

        // Authenticate user
        user = securityService.authenticate(username, new String(password));

        if (user == null) {
            throw new FailedLoginException("Invalid username or password");
        }

        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        // Add user principal to subject
        Principal userPrincipal = user::getUsername;
        subject.getPrincipals().add(userPrincipal);

        // Add roles to subject
        user.getRoles().forEach(role -> {
            Principal rolePrincipal = () -> role;
            subject.getPrincipals().add(rolePrincipal);
        });

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        logout();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        user = null;
        return true;
    }
}
