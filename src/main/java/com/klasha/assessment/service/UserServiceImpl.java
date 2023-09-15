package com.klasha.assessment.service;

import com.klasha.assessment.entity.User;
import com.klasha.assessment.exception.DuplicateUserException;
import com.klasha.assessment.exception.EntityNotFoundException;
import com.klasha.assessment.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();

    @Override
    public User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return unwrapUser(user, 404L);
    }

    @Override
    public User saveUser(User user) {
        if(doesUserExist(user.getUsername()))
        {
            throw new DuplicateUserException("User with the provided information already exists. Please choose a different username");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    static User unwrapUser(Optional<User> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, User.class);
    }

    Boolean doesUserExist(String username)
    {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
