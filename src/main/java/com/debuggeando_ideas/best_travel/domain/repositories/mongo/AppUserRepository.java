package com.debuggeando_ideas.best_travel.domain.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUserRepository, String> {

    Optional<AppUserRepository> findByUsername(String username);
}
