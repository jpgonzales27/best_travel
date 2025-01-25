package com.debuggeando_ideas.best_travel.domain.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUserRepository, String> {
}
