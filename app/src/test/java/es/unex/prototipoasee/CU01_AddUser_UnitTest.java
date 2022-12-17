package es.unex.prototipoasee;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import es.unex.prototipoasee.model.User;

public class CU01_AddUser_UnitTest {

    protected User user;

    @Test
    public void shouldGetUsername() {
        assertEquals(user.getUsername(),"username");
    }

    @Test
    public void shouldGetEmail() {
        assertEquals(user.getEmail(),"user@email.es");
    }

    @Test
    public void shouldGetPassword() {
        assertEquals(user.getPassword(),"password");
    }

    @Test
    public void shouldSetUsername() {
        user.setUsername("user");
        assertEquals(user.getUsername(),"user");
    }

    @Test
    public void shouldSetEmail() {
        user.setEmail("user@unex.es");
        assertEquals(user.getEmail(),"user@unex.es");
    }

    @Test
    public void shouldSetPassword() {
        user.setPassword("pass");
        assertEquals(user.getPassword(),"pass");
    }

    @Before
    public void initTest() {
        user = new User ("username","user@email.es","password");
    }
}
