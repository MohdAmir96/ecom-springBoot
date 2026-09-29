package com.amir.ecommerce.repo;

import com.amir.ecommerce.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
