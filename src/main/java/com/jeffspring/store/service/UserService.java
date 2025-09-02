package com.jeffspring.store.service;

import com.jeffspring.store.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User createUser(User user);
    boolean checkEmail(String email);
    User findByEmail(String email);
}
