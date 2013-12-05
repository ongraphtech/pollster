package com.impl.pollster

class BootstrapService {
    void createUser() {
        if (!User.count()) {
            new User(userName: 'username1', password:'password1',email: 'a@a.com', name: 'User 1').save(flush: true)
            new User(userName: 'username2', password:'password2',email: 'b@a.com', name: 'User 2').save(flush: true)
        }
    }


    void createStatus() {
        if (!Status.count()) {
            new Status(upVote: 0, downVote:0, statusString: 'Test status by User 1', postedBy: 'User 1').save(flush: true)
            new Status(upVote: 0, downVote:0, statusString: 'Test status by User 2', postedBy: 'User 2').save(flush: true)
            new Status(upVote: 0, downVote:1, statusString: 'Test status by User 1 Second time', postedBy: 'User 1').save(flush: true)
            new Status(upVote: 1, downVote:0, statusString: 'Test status by User 2 Second time', postedBy: 'User 2').save(flush: true)
        }
    }

}