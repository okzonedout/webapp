package com.csye6225.assignment.security;

import com.csye6225.assignment.model.Account;
import com.csye6225.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account user = this.repository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("User not found : " + username);
        return new CustomUserDetail(user);
    }
}
