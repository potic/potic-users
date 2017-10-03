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
}
