package com.jeffspring.store.service;

import com.jeffspring.store.model.User;
import com.jeffspring.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    @Autowired
    public UserServiceImpl(UserRepository repo){
        this.repository = repo;
    }
    @Override
    public User createUser(User user){

        return repository.save(user);
    }
    @Override
    public boolean checkEmail(String email){
        return repository.existsByEmail(email);
    }
    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
