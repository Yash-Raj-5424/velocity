package com.velocity.userservice.repositories;

import com.velocity.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Boolean existsByEmail(String email);

    Boolean existsByKeycloakId(String id);

    User findByEmail(String email);
}
