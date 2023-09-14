package com.klasha.assessment.service;


import com.klasha.assessment.entity.User;

public interface UserService {
    User getUser(Long id);
    User getUser(String username);
    User saveUser(User user);

}