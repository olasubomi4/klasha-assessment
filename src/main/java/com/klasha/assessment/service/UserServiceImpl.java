package com.klasha.assessment.service;

import com.klasha.assessment.entity.User;
import com.klasha.assessment.exception.DuplicateUserException;
import com.klasha.assessment.exception.EntityNotFoundException;
import com.klasha.assessment.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
    private static Logger log = LogManager.getLogger(UserServiceImpl.class);

    @Override
    public User getUser(String username) {
        try
        {
        Optional<User> user = userRepository.findByUsername(username);
        return unwrapUser(user, 404L);
        }
        catch (Exception e)
        {
            log.error("exception thrown while trying to get user "+username);
            throw new RuntimeException("exception thrown while trying to get user "+username,e);
        }
    }

    @Override
    public User saveUser(User user) {
    
        if (doesUserExist(user.getUsername())) {
            log.info("User with the provided information already exists. Please choose a different username");
            throw new DuplicateUserException("User with the provided information already exists. Please choose a different username");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        
        try {
            return userRepository.save(user);
        }
        catch (Exception e)
        {
            log.error("exception thrown while trying to save user "+user.getUsername());
            throw new RuntimeException("exception thrown while trying to save user "+ user.getUsername(),e);
        }
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
