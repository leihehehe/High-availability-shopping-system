package com.leih.userservice.dao;

import com.leih.shopping.model.User;

import java.util.List;

public interface UserDao {
    public User getUserByUsernamePasswd(String username, String password);
}
