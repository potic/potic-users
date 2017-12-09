package me.potic.users.service

import com.google.common.base.Ticker
import com.stehno.ersatz.ErsatzServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.DAYS
import static java.util.concurrent.TimeUnit.NANOSECONDS
import static org.hamcrest.Matchers.equalTo

@SpringBootTest
@ActiveProfiles('integrationTest')
class Auth0ServiceTest extends Specification {

    @Autowired
    Auth0Service auth0Service

    def 'String getSocialId(String auth0Token)'(){
        setup: 'mock server instead of actual Auth0'
        ErsatzServer ersatz = new ErsatzServer()
        ersatz.expectations {
            get('/userinfo') {
                called equalTo(1)
                header 'Authorization', equalTo('Bearer TEST_TOKEN_28')
                responder {
                    content '{"sub":"google-oauth2|28","name":"Yaroslav Yermilov"}','application/json'
                }
            }
        }
        ersatz.start()

        and: 'instruct service to use mock server'
        auth0Service.auth0Rest(ersatz.httpUrl)

        when: 'get socialId by auth0 token'
        String actualSocialId = auth0Service.getSocialId('TEST_TOKEN_28')

        then: 'expected token is returned'
        actualSocialId == 'google-oauth2|28'

        and: 'mock server received expected calls'
        ersatz.verify()

        cleanup: 'stop mock server'
        ersatz.stop()
    }

    def 'String getSocialId(String auth0Token) - results are cached'(){
        setup: 'mock servers'
        ErsatzServer ersatz = new ErsatzServer()
        ersatz.expectations {
            get('/userinfo') {
                called equalTo(1)
                header 'Authorization', equalTo('Bearer TEST_TOKEN_43')
                responder {
                    content '{"sub":"google-oauth2|43","name":"Yaroslav Yermilov"}','application/json'
                }
            }
        }
        ersatz.start()

        and: 'instruct service to use mock server'
        auth0Service.auth0Rest(ersatz.httpUrl)

        when: 'get socialId by auth0 token first time'
        String actualSocialId1 = auth0Service.getSocialId('TEST_TOKEN_43')

        then: 'expected token is returned'
        actualSocialId1 == 'google-oauth2|43'

        when: 'get socialId by auth0 token second time'
        String actualSocialId2 = auth0Service.getSocialId('TEST_TOKEN_43')

        then: 'expected token is returned'
        actualSocialId2 == 'google-oauth2|43'

        and: 'mock server received expected calls'
        ersatz.verify()

        cleanup: 'stop mock server'
        ersatz.stop()
    }

    def 'String getSocialId(String auth0Token) - cached results are expiring'(){
        setup: 'mock servers'
        ErsatzServer ersatz = new ErsatzServer()
        ersatz.expectations {
            get('/userinfo') {
                called equalTo(2)
                header 'Authorization', equalTo('Bearer TEST_TOKEN_17')
                responder {
                    content '{"sub":"google-oauth2|17","name":"Yaroslav Yermilov"}','application/json'
                }
            }
        }
        ersatz.start()

        and: 'instruct service to use mock server'
        auth0Service.auth0Rest(ersatz.httpUrl)

        and: 'instruct service to use mock time'
        Ticker ticker = Mock()
        ticker.read() >>> [ 0L, 0L, NANOSECONDS.convert(2, DAYS) ]
        auth0Service.cachedSocialIds(ticker)

        when: 'get socialId by auth0 token first time'
        String actualSocialId1 = auth0Service.getSocialId('TEST_TOKEN_17')

        then: 'expected token is returned'
        actualSocialId1 == 'google-oauth2|17'

        when: 'two days passed, get socialId by auth0 token second time'
        String actualSocialId2 = auth0Service.getSocialId('TEST_TOKEN_17')

        then: 'expected token is returned'
        actualSocialId2 == 'google-oauth2|17'

        and: 'mock server received expected calls'
        ersatz.verify()

        cleanup: 'stop mock server'
        ersatz.stop()
    }
}