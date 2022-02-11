package com.aueb.webapp.security.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aueb.webapp.WebappApplication;
import com.aueb.webapp.models.Logging;
import com.aueb.webapp.models.User;
import com.aueb.webapp.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserLoginService userService;

    @Autowired
    LogRepository logRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsImpl userDetails =  (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user.getUsername());
        }
        logRepo.save(new Logging(user.getUsername(), new Date(), true));
        if(userService.passwordChangeRequired(user)) {
            WebappApplication.loginMessage= "Your password is expired and needs to be changed";
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
