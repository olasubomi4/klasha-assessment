package com.klasha.assessment.service;


import com.klasha.assessment.entity.User;

public interface UserService {

    User getUser(String username);
    User saveUser(User user);

}