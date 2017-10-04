package me.potic.users.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.users.domain.User
import me.potic.users.service.Auth0Service
import me.potic.users.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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
        log.info 'receive GET request for /user/me'

        try {
            String socialId = auth0Service.getSocialId(principal.token)
            return userService.findUserBySocialId(socialId)
        } catch (e) {
            log.error "GET request for /user/me failed: $e.message", e
            throw new RuntimeException("GET request for /user/me failed: $e.message", e)
        }
    }

    @Timed(name = 'user')
    @CrossOrigin
    @GetMapping(path = '/user')
    @ResponseBody List<User> getAllUsers() {
        log.info 'receive GET request for /users'

        try {
            return userService.getAllUsers()
        } catch (e) {
            log.error "GET request for /users failed: $e.message", e
            throw new RuntimeException("GET request for /users failed: $e.message", e)
        }
    }

    @Timed(name = 'user.POST')
    @CrossOrigin
    @PostMapping(path = '/user')
    @ResponseBody User createNewUser() {
        log.info 'receive POST request for /user'

        try {
            return userService.createNewUser()
        } catch (e) {
            log.error "POST request for /user failed: $e.message", e
            throw new RuntimeException("POST request for /user failed: $e.message", e)
        }
    }

    @Timed(name = 'user.register.socialId')
    @CrossOrigin
    @PostMapping(path = '/user/{userId}/register/socialId/{socialId}')
    @ResponseBody User registerSocialId(@PathVariable('userId') String userId, @PathVariable(value = 'socialId') String socialId) {
        log.info "receive POST request for /user/$userId/register/socialId"

        try {
            return userService.registerSocialId(userId, socialId)
        } catch (e) {
            log.error "receive POST request for /user/$userId/register/socialId failed: $e.message", e
            throw new RuntimeException("receive POST request for /user/$userId/register/socialId failed: $e.message", e)
        }
    }

    @Timed(name = 'user.register.pocketAccessToken')
    @CrossOrigin
    @PostMapping(path = '/user/{userId}/register/pocketAccessToken/{pocketAccessToken}')
    @ResponseBody User registerPocketAccessToken(@PathVariable('userId') String userId, @PathVariable(value = 'pocketAccessToken') String pocketAccessToken) {
        log.info "receive POST request for /user/$userId/register/pocketAccessToken"

        try {
            return userService.registerPocketAccessToken(userId, pocketAccessToken)
        } catch (e) {
            log.error "receive POST request for /user/$userId/register/pocketAccessToken failed: $e.message", e
            throw new RuntimeException("receive POST request for /user/$userId/register/pocketAccessToken failed: $e.message", e)
        }
    }
}
