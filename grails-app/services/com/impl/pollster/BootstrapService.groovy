package com.impl.pollster

class BootstrapService {
    void createUser() {
        if (!User.count) {
            new User(userName: 'user1', password: 'pass1', email: 'a@a.com', name: 'User 1').save(flush: true)
            new User(userName: 'user2', password: 'pass2', email: 'b@a.com', name: 'User 2').save(flush: true)
            new User(userName: 'user3', password: 'pass3', email: 'c@a.com', name: 'User 3').save(flush: true)
            new User(userName: 'user4', password: 'pass4', email: 'd@a.com', name: 'User 4').save(flush: true)
            new User(userName: 'user5', password: 'pass5', email: 'e@a.com', name: 'User 5').save(flush: true)
            new User(userName: 'user6', password: 'pass6', email: 'f@a.com', name: 'User 6').save(flush: true)
        }
    }


    void createStatus() {
        if (!Status.count) {
            new Status(upVote: 0, downVote: 0, statusString: 'Test status by User 1', postedBy: 'User 1').save(flush: true)
            new Status(upVote: 0, downVote: 0, statusString: 'Test status by User 2', postedBy: 'User 2').save(flush: true)
            new Status(upVote: 0, downVote: 1, statusString: 'Test status by User 1 Second time', postedBy: 'User 1').save(flush: true)
            new Status(upVote: 1, downVote: 0, statusString: 'Test status by User 2 Second time', postedBy: 'User 2').save(flush: true)
        }
    }

    void createOptions() {
        Status status = Status.get('1')
        if (!StatusOption.count) {
            new StatusOption(statusText: 'Opton 1', status: status).save(flush: true)
            new StatusOption(statusText: 'Opton 2', status: status).save(flush: true)
            new StatusOption(statusText: 'Opton 3', status: status).save(flush: true)
            new StatusOption(statusText: 'Opton 4', status: status).save(flush: true)
            Status status2 = Status.get('2')
            new StatusOption(statusText: 'Opton 1 for 2', status: status2).save(flush: true)
            new StatusOption(statusText: 'Opton 2 for 2', status: status2).save(flush: true)
            new StatusOption(statusText: 'Opton 3 for 2', status: status2).save(flush: true)
            new StatusOption(statusText: 'Opton 4 for 2', status: status2).save(flush: true)
        }
    }

    void createPollOpenion() {
        if (!PollOpinion.count) {
            new PollOpinion(user: User.get('1'), status: Status.get('1'), statusOption: StatusOption.get('1')).save(flush: true)
            new PollOpinion(user: User.get('2'), status: Status.get('1'), statusOption: StatusOption.get('2')).save(flush: true)
            new PollOpinion(user: User.get('3 '), status: Status.get('1'), statusOption: StatusOption.get('3')).save(flush: true)
            new PollOpinion(user: User.get('4'), status: Status.get('1'), statusOption: StatusOption.get('4')).save(flush: true)
            new PollOpinion(user: User.get('1'), status: Status.get('2'), statusOption: StatusOption.get('5')).save(flush: true)
            new PollOpinion(user: User.get('2'), status: Status.get('2'), statusOption: StatusOption.get('6')).save(flush: true)
            new PollOpinion(user: User.get('3'), status: Status.get('3'), statusOption: StatusOption.get('7')).save(flush: true)
            new PollOpinion(user: User.get('4'), status: Status.get('4'), statusOption: StatusOption.get('5')).save(flush: true)
        }
    }

}