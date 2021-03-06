package com.impl.pollster

class Status {
    Integer upVote = 0
    Integer downVote = 0
    String statusString
    String postedBy
    Date dateCreated
    Date lastUpdated

    static hasMany = [statusOptions: StatusOption, pollOpinions: PollOpinion]

    static mapping = {
        statusOptions sort: 'id', order: 'asc'
    }

    static constraints = {
        statusString(blank: false, nullable: false)
        postedBy(blank: false, nullable: false)
    }
}