package com.aueb.webapp.security.services;

import com.aueb.webapp.models.User;
import com.aueb.webapp.repositories.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@AllArgsConstructor
public class UserLoginService implements UserDetailsService {

    private final String USERNAME_NOT_EXISTS = "Username %s not found";
    private final UsersRepository repository;
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 60 * 1000; // unlock user after a period of time (in msec)
    private static final long PWD_PERIOD_VALIDITY = 60 * 60 * 1000; //period after which password change is required (in msec)

    /* load user by username and return UserDetails object to be used for authentication */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(String.format(USERNAME_NOT_EXISTS, username))); //find user from database
        return UserDetailsImpl.build(user); //create UserDetails object and return it
    }

    public User findByName(String username){
        return repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(String.format(USERNAME_NOT_EXISTS, username)));
    }

    public void incrementFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        repository.updateFailedAttempts(newFailAttempts, user.getUsername());
    }

    public void resetFailedAttempts(String username) {
        repository.updateFailedAttempts(0, username);
    }

    public void lock(User user) {
        user.setLocked(true);
        user.setLockTime(new Date());

        repository.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setLocked(false);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            repository.save(user);

            return true;
        }

        return false;
    }

    public boolean passwordChangeRequired(User user){
        return user.getLastModified().getTime() + PWD_PERIOD_VALIDITY < (new Date()).getTime();
    }
}
