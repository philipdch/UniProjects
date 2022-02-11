package com.aueb.webapp.security.services;

import com.aueb.webapp.models.Logging;
import com.aueb.webapp.models.LoggingCompositeKey;
import com.aueb.webapp.models.User;
import com.aueb.webapp.repositories.LogRepository;
import com.aueb.webapp.security.services.UserDetailsImpl;
import com.aueb.webapp.security.services.UserLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserLoginService userService;

    @Autowired
    LogRepository logRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        logRepo.save(new Logging(username, new Date(), false));
        User user = userService.findByName(username);
        UserDetails userDetails = UserDetailsImpl.build(user);
        if (user != null) {
            if (user.isEnabled() && userDetails.isAccountNonLocked()) {
                if (user.getFailedAttempts() < UserLoginService.MAX_FAILED_ATTEMPTS - 1) {
                    userService.incrementFailedAttempts(user);
                } else {
                    System.out.println("Locking account");
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 1 minute.");
                }
            } else if (!userDetails.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }
        }
        //reply with error message
        response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
        Map<String, Object> data = new HashMap<>();
        data.put(
                "timestamp",
                Calendar.getInstance().getTime());
        data.put(
                "exception",
                exception.getMessage());
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
}
