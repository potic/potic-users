package me.potic.users.service

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.users.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query

@Service
@Slf4j
class UserService {

    @Autowired
    MongoTemplate mongoTemplate

    @Timed(name = 'findUserBySocialId')
    User findUserBySocialId(String socialId) {
        log.info 'finding user id by social id'

        try {
            Collection<User> result = mongoTemplate.find(query(where('socialIds').is(socialId)), User)

            if (result.size() == 0) {
                return null
            }
            if (result.size() > 1) {
                throw new IllegalStateException('there are more than one user with same social id')
            }

            return result.first()
        } catch (e) {
            log.error "finding user id by social id failed: $e.message", e
            throw new RuntimeException('finding user id by social id failed', e)
        }
    }

    List<String> getAllUsersIds() {
        log.info 'finding all user ids'

        try {
            return mongoTemplate.findAll(User).collect({ user -> user.id })
        } catch (e) {
            log.error "finding all user ids failed: $e.message", e
            throw new RuntimeException('finding all user ids failed', e)
        }
    }

    User createNewUser() {
        log.info 'creating new user'

        try {
            User user = new User()
            mongoTemplate.save(user)
            return user
        } catch (e) {
            log.error "creating new user failed: $e.message", e
            throw new RuntimeException('creating new user failed', e)
        }
    }

    User registerSocialId(String userId, String socialId) {
        log.info "registering social id for user $userId"

        try {
            User user = mongoTemplate.find(query(where('id').is(userId)), User).first()
            user.socialIds = user.socialIds == null ? [ socialId ] : user.socialIds + socialId
            mongoTemplate.save(user)
            return user
        } catch (e) {
            log.error "registering social id for user $userId failed: $e.message", e
            throw new RuntimeException("registering social id for user $userId failed", e)
        }
    }

    User registerPocketAccessToken(String userId, String pocketAccessToken) {
        log.info "registering pocket access token for user $userId"

        try {
            User user = mongoTemplate.find(query(where('id').is(userId)), User).first()
            user.pocketAccessToken = pocketAccessToken
            mongoTemplate.save(user)
            return user
        } catch (e) {
            log.error "registering pocket access token for user $userId failed: $e.message", e
            throw new RuntimeException("registering pocket access token for user $userId failed", e)
        }
    }
}
