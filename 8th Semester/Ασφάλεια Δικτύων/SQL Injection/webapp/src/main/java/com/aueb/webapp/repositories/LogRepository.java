package com.aueb.webapp.repositories;

import com.aueb.webapp.models.Logging;
import com.aueb.webapp.models.LoggingCompositeKey;
import com.aueb.webapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LogRepository extends JpaRepository<Logging, LoggingCompositeKey> {

    List<Logging> findByUsername(String username);
}
