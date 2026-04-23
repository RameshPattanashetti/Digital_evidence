package com.digitalevidence.mvc.mongodb.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.digitalevidence.mvc.mongodb.model.User;

@Profile("mongo")
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
