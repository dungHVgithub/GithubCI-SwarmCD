package com.smartStudy.repositories;
import  com.smartStudy.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    List<User> getUsers (Map<String,String> params);
    User getUserById(int id);
    User getUserByMail(String email);
    void deleteUser(int id);
    User updateUser(User u);
    boolean authenticate (String email, String password);
    long countUsers();
}
