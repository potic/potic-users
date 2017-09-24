package me.potic.users.domain

import org.springframework.data.annotation.Id

class User {

    @Id
    String id

    String name

    String accessToken
}
