package com.project.mygym.contact;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
public class EmailAddress {
    public static Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Column(name = "email")
    private String value;

    public EmailAddress(String email) {
        this.value = email;
    }

    public EmailAddress() {}

    public String getAddress() {
        return value;
    }

    public boolean isValid() {
        return value!=null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EmailAddress)) {
            return false;
        }

        if (this == other) {
            return true;
        }

        EmailAddress email = (EmailAddress) other;
        return value == null ? email.getAddress() == null
                : value.equals(email.getAddress());
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    /* Uses the predefined pattern to check if a given string
        corresponds to a valid email address
     */
    public static boolean validate(String email){
        Matcher matcher = VALID_EMAIL_PATTERN.matcher(email);
        return matcher.find();
    }
}