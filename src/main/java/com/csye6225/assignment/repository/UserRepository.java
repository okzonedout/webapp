package com.csye6225.assignment.repository;

import com.csye6225.assignment.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Account, String> {
    Account findByUsername(String username);
}
