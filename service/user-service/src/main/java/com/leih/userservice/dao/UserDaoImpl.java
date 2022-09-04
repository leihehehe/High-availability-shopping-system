package com.leih.userservice.dao;


import com.leih.shopping.model.Product;
import com.leih.shopping.model.User;
import com.leih.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserDaoImpl implements UserDao {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User getUserByUsernamePasswd(String username, String password) {
        return userMapper.getUserByUsernamePasswd(username,password);
    }
}
