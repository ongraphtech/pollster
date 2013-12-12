package com.impl.pollster

class PollOpinion {
    Boolean active = true
    User user
    Date dateCreated
    Date lastUpdated

    static belongsTo = [status: Status, statusOption: StatusOption]

    static constraints = {
    }
}
