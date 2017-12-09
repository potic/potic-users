package me.potic.users.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.users.domain.User
import me.potic.users.service.Auth0Service
import me.potic.users.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import java.security.Principal

import static me.potic.users.util.Utils.maskForLog

@RestController
@Slf4j
class UserController {

    @Autowired
    UserService userService

    @Autowired
    Auth0Service auth0Service

    @CrossOrigin
    @GetMapping(path = '/user/me')
    @ResponseBody User findUserByAuth0Token(final Principal principal) {
        log.info "receive GET request for /user/me with token=${maskForLog(principal.token)}"

        try {
            String socialId = auth0Service.getSocialId(principal.token)
            return userService.findUserBySocialId(socialId)
        } catch (e) {
            log.error "GET request for /user/me with token=${maskForLog(principal.token)} failed: $e.message", e
            throw new RuntimeException("GET request for /user/me with token=${maskForLog(principal.token)} failed: $e.message", e)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user')
    @ResponseBody List<User> getAllUsers() {
        log.info 'receive GET request for /user'

        try {
            return userService.getAllUsers()
        } catch (e) {
            log.error "GET request for /user failed: $e.message", e
            throw new RuntimeException("GET request for /user failed: $e.message", e)
        }
    }

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

    @CrossOrigin
    @PostMapping(path = '/user/{userId}/register/socialId/{socialId}')
    @ResponseBody User registerSocialId(@PathVariable('userId') String userId, @PathVariable(value = 'socialId') String socialId) {
        log.info "receive POST request for /user/$userId/register/socialId/${maskForLog(socialId)}"

        try {
            return userService.registerSocialId(userId, socialId)
        } catch (e) {
            log.error "POST request for /user/$userId/register/socialId/${maskForLog(socialId)} failed: $e.message", e
            throw new RuntimeException("POST request for /user/$userId/register/socialId/${maskForLog(socialId)} failed: $e.message", e)
        }
    }

    @Timed(name = 'user.register.pocketAccessToken')
    @CrossOrigin
    @PostMapping(path = '/user/{userId}/register/pocketAccessToken/{pocketAccessToken}')
    @ResponseBody User registerPocketAccessToken(@PathVariable('userId') String userId, @PathVariable(value = 'pocketAccessToken') String pocketAccessToken) {
        log.info "receive POST request for /user/$userId/register/pocketAccessToken/${maskForLog(pocketAccessToken)}"

        try {
            return userService.registerPocketAccessToken(userId, pocketAccessToken)
        } catch (e) {
            log.error "POST request for /user/$userId/register/pocketAccessToken/${maskForLog(pocketAccessToken)} failed: $e.message", e
            throw new RuntimeException("POST request for /user/$userId/register/pocketAccessToken/${maskForLog(pocketAccessToken)} failed: $e.message", e)
        }
    }
}
