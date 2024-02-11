package com.lcwd.user.service.services;

import com.lcwd.user.service.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {

    //create user
    User saveUser(User user);

    //Get All User
    List<User> getAllUser();

    //get single user by Id
    User getUser(String userId);



}
