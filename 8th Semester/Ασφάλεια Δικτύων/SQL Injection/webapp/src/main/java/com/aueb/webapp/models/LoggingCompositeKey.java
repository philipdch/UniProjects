package com.aueb.webapp.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class LoggingCompositeKey implements Serializable {

    private String username;

    private Date loginAttempt;

    public LoggingCompositeKey(){}

    public LoggingCompositeKey(String username, Date loginAttempt) {
        this.username = username;
        this.loginAttempt = loginAttempt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggingCompositeKey key = (LoggingCompositeKey) o;
        return username.equals(key.username) &&
                loginAttempt.equals(key.loginAttempt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, loginAttempt);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLogin_attempt() {
        return loginAttempt;
    }

    public void setLogin_attempt(Date login_attempt) {
        this.loginAttempt = login_attempt;
    }
}

