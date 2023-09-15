package com.klasha.assessment.service;

import com.klasha.assessment.entity.User;
import com.klasha.assessment.exception.DuplicateUserException;
import com.klasha.assessment.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService= new UserServiceImpl();

    @Test
    public void testRegisterUser_success() {
        //Building new user object
        User newUser= new User();
        newUser.setUsername("johnWick");
        newUser.setPassword("superman");


        //mocking the entity response
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());

        //performing the test
        userService.saveUser(newUser);

        // Verifying that the user was registered in the database
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void testRegisterUser_errorRegisterExistingUser() {

        //Building new user object
        User newUser= new User();
        newUser.setId(1);
        newUser.setUsername("johnWick");
        newUser.setPassword("superman");

        //Building existing user object
        User existingUser= new User();
        existingUser.setId(1);
        existingUser.setUsername("johnWick");
        existingUser.setPassword("superman");


        //mocking the entity response
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.of(existingUser));

        // Verify the result
        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy(()->
                {
                    //Performing the test
                    userService.saveUser(newUser);
                }

        ).withMessage("User with the provided information already exists. Please choose a different username");

        // Verify that the user was not registered in the database
        verify(userRepository, times(0)).save(newUser);
    }
}
