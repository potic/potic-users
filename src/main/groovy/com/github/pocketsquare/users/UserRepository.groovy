package com.github.pocketsquare.users

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
interface UserRepository extends MongoRepository<User, String> {
}