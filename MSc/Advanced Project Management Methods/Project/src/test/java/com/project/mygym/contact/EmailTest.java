package com.project.mygym.contact;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmailTest {

    EmailAddress validEmail1 = new EmailAddress("test.15@aueb.gr");
    EmailAddress validEmail2 = new EmailAddress("test2@aueb.gr");
    EmailAddress invalidEmail = new EmailAddress("invmail@.com");
    EmailAddress nullEmail = new EmailAddress(null);

    @Test
    public void testEquals(){
        Assertions.assertEquals(validEmail1, new EmailAddress("test.15@aueb.gr"));
        Assertions.assertEquals(validEmail1, validEmail1);
        Assertions.assertEquals(nullEmail, nullEmail);
        Assertions.assertNotEquals(validEmail1, validEmail2);
        Assertions.assertNotEquals(validEmail1, "abc@test.com");
    }

    @Test
    public void testValidEmail(){
        Assertions.assertTrue(validEmail1.isValid());
        Assertions.assertFalse(nullEmail.isValid());
        Assertions.assertTrue(EmailAddress.validate(validEmail1.getAddress()));
        Assertions.assertFalse(EmailAddress.validate(invalidEmail.toString()));
    }
}
