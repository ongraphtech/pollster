package com.impl.pollster

class User {
    String userName
    String password
    String email
    String name

    static constraints = {
        userName(nullable: false, blank: false)
        password(nullable: false, blank: false)
        email(nullable: false, blank: false)
        name(nullable: false, blank: false)
    }
}
