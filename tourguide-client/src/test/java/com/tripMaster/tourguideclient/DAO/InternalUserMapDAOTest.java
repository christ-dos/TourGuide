package com.tripMaster.tourguideclient.DAO;

import com.tripMaster.tourguideclient.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InternalUserMapDAOTest {
    private InternalUserMapDAO internalUserMapDAO;

    @BeforeEach
    public void setUpPerTest() {
        internalUserMapDAO = new InternalUserMapDAO();
    }

    @Test
    public void addUserTest() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user1 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        //WHEN
        internalUserMapDAO.addUser(user);
        internalUserMapDAO.addUser(user1);

        User retrivedUser = internalUserMapDAO.getUser(user.getUserName());
        User retrivedUser1 = internalUserMapDAO.getUser(user1.getUserName());
        //THEN
        assertEquals(user, retrivedUser);
        assertEquals(user1, retrivedUser1);
    }

    @Test
    public void getAllUsersTest_thenReturnListWithTwoUsersWhichContainUserAndUserOne() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user1 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        //WHEN
        internalUserMapDAO.addUser(user);
        internalUserMapDAO.addUser(user1);

        List<User> allUsers = internalUserMapDAO.getAllUsers();
        //THEN
        assertTrue(allUsers.size() > 0);
        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user1));
    }

    @Test
    public void getUserTest_whenUserExistAndUserNameIsJon_thenReturnUser() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //WHEN
        internalUserMapDAO.addUser(user);

        User userResult = internalUserMapDAO.getUser("jon");
        //THEN
        assertNotNull(userResult);
        assertEquals(user.getUserId(),userResult.getUserId());
    }

    @Test
    public void getUserTest_whenUserNotExist_thenReturnNull() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //WHEN
        internalUserMapDAO.addUser(user);

        User userResult = internalUserMapDAO.getUser("UnKnown");
        //THEN
        assertNull(userResult);
    }
}
