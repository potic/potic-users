package me.potic.users.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.users.domain.User
import me.potic.users.service.Auth0Service
import me.potic.users.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@RestController
@Slf4j
class UserController {

    @Autowired
    UserService userService

    @Autowired
    Auth0Service auth0Service

    @Timed(name = 'user.me')
    @CrossOrigin
    @GetMapping(path = '/user/me')
    @ResponseBody User findUserByAuth0Token(final Principal principal) {
        log.info 'receive request for /user/me'

        try {
            String socialId = auth0Service.getSocialId(principal.token)
            return userService.findUserBySocialId(socialId)
        } catch (e) {
            log.error "request for /user/me failed: $e.message", e
            throw new RuntimeException("request for /user/me failed: $e.message", e)
        }
    }
}
