package me.potic.users.service

import com.google.common.base.Ticker
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

import static me.potic.users.util.Utils.maskForLog

@Service
@Slf4j
class Auth0Service {

    HttpBuilder auth0Rest

    LoadingCache<String, String> cachedSocialIds

    @Autowired
    HttpBuilder auth0Rest(@Value('${services.auth0.url}') String auth0ServiceUrl) {
        auth0Rest = HttpBuilder.configure {
            request.uri = auth0ServiceUrl
        }
    }

    @PostConstruct
    void initCachedSocialIds() {
        cachedSocialIds(Ticker.systemTicker())
    }

    LoadingCache<String, String> cachedSocialIds(Ticker ticker) {
        cachedSocialIds = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .ticker(ticker)
                .build(
                        new CacheLoader<String, String>() {

                            @Override
                            String load(String auth0Token) {
                                requestSocialId(auth0Token)
                            }
                        }
                )
    }

    String getSocialId(String auth0Token) {
        log.debug "getting social id of user with token ${maskForLog(auth0Token)}"

        try {
            return cachedSocialIds.get(auth0Token)
        } catch (e) {
            log.error "getting social id of user with token ${maskForLog(auth0Token)} failed: $e.message", e
            throw new RuntimeException("getting social id of user with token ${maskForLog(auth0Token)} failed", e)
        }
    }

    String requestSocialId(String auth0Token) {
        log.info "requesting auth0 for social id of user with token ${maskForLog(auth0Token)}"

        try {
            def authResult = auth0Rest.get {
                request.uri.path = '/userinfo'
                request.headers['Authorization'] = 'Bearer ' + auth0Token
            }

            return authResult['sub']
        } catch (e) {
            log.error "requesting auth0 for social id of user with token ${maskForLog(auth0Token)} failed: $e.message", e
            throw new RuntimeException("requesting auth0 for social id of user with token ${maskForLog(auth0Token)} failed", e)
        }
    }
}
