package com.leih.userservice.mapper;

import com.leih.shopping.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    User getUserByUsernamePasswd(String username, String password);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}