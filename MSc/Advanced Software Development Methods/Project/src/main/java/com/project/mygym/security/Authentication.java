package com.project.mygym.security;

import com.project.mygym.contact.EmailAddress;
import com.project.mygym.domain.SimpleUser;
import com.project.mygym.domain.Trainer;
import com.project.mygym.domain.User;
import com.project.mygym.persistence.SimpleUserRepository;
import com.project.mygym.persistence.TrainerRepository;
import com.project.mygym.utils.SecurityImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.project.mygym.utils.SecurityImpl.checkPassword;

@RequestScoped
public class Authentication {

    @Inject
    EntityManager em;
    @Inject
    SimpleUserRepository simpleUserRepository;

    @Inject
    TrainerRepository trainerRepository;

    @Transactional
    public boolean register(User user){
        if(user == null || user.getPassword() == null || user.getPassword().length == 0) return false;
        List<byte[]> saltedPassword = SecurityImpl.hashPassword(new String(user.getPassword()));
        byte[] hashedPassword = saltedPassword.get(1);
        byte[] salt = saltedPassword.get(0);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        if(EmailAddress.validate(user.getEmail()) && user.getPassword() != null){
            user.setLoggedIn(true);
            if(user instanceof Trainer)
                trainerRepository.persist((Trainer) user);
            else
                simpleUserRepository.persist((SimpleUser) user);
            return true;
        }
        return false;
    }

    public boolean login(String email, String givenPassword){
        System.out.println(email);
        System.out.println(givenPassword);
        Query queryUser = em.createQuery("SELECT user FROM User user where user.email = :userEmail");
        queryUser.setParameter("userEmail", email.toLowerCase());
        List<User> userResults = queryUser.getResultList();
        if(userResults.isEmpty()) return false;
        User user = userResults.get(0);
        if(user != null && checkPassword(givenPassword, user)){
            user.setLoggedIn(true);

            return true;
        }
        return false;
    }
}
