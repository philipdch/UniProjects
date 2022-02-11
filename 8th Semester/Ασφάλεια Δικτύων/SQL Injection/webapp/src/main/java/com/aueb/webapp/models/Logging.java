package com.aueb.webapp.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = "logging", schema = "public")
@IdClass(LoggingCompositeKey.class)
public class Logging {

    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Id
    @Column(name = "login_attempt_time", nullable = false)
    private Date loginAttempt;

    @Column(name = "success", nullable = false)
    private boolean success;

    public Logging(){}

    public Logging(String username, Date loginAttempt, boolean success) {
        this.username = username;
        this.loginAttempt = loginAttempt;
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLoginAttempt() {
        return loginAttempt;
    }

    public void setLoginAttempt(Date loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
