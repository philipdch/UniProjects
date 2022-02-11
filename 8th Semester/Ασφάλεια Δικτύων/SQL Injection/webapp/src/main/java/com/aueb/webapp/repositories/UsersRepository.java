package com.aueb.webapp.repositories;


import com.aueb.webapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UsersRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String name); //returns user or null if username doesn't exist

    @Query("UPDATE User u SET u.failedAttempts = ?1 WHERE u.username = ?2")
    @Modifying
    public void updateFailedAttempts(int failAttempts, String username);
}
