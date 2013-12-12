package com.impl.pollster

class StatusOption {
    String statusText
    Boolean active = true
    Date dateCreated
    Date lastUpdated

    static belongsTo = [status: Status]

    static constraints = {
        statusText(nullable: false, blank: false)
        status(nullable: false)
    }
}
