package com.impl.pollster

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import static javax.servlet.http.HttpServletResponse.*

class StatusController {

    def emailService
    static final int SC_UNPROCESSABLE_ENTITY = 422

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() { }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        response.setIntHeader('X-Pagination-Total', Status.count())
        render Status.list(params) as JSON
    }

    def save() {
        def statusInstance = new Status(request.JSON)
        statusInstance.postedBy = session?.user?.name
        def responseJson = [:]
        if (statusInstance.save(flush: true)) {
            response.status = SC_CREATED
            responseJson.id = statusInstance.id
            responseJson.message = message(code: 'default.created.message', args: [message(code: 'status.label', default: 'Status'), statusInstance.id])
        } else {
            response.status = SC_UNPROCESSABLE_ENTITY
            responseJson.errors = statusInstance.errors.fieldErrors.collectEntries {
                [(it.field): message(error: it)]
            }
        }
        render responseJson as JSON
    }

    def fetch() {
        def statusInstance = Status.get(params.id)
        if (statusInstance) {
            render statusInstance as JSON
        } else {
            notFound params.id
        }
    }

    def update() {
        def statusInstance = Status.get(params.id)
        if (!statusInstance) {
            notFound params.id
            return
        }
        def responseJson = [:]
        if (request.JSON.version != null) {
            if (statusInstance.version > request.JSON.version) {
                response.status = SC_CONFLICT
                responseJson.message = message(code: 'default.optimistic.locking.failure',
                        args: [message(code: 'status.label', default: 'Status')],
                        default: 'Another user has updated this Status while you were editing')
                cache false
                render responseJson as JSON
                return
            }
        }

        statusInstance.properties = request.JSON

        if (statusInstance.save(flush: true)) {
            response.status = SC_OK
            responseJson.id = statusInstance.id
            responseJson.message = message(code: 'default.updated.message', args: [message(code: 'status.label', default: 'Status'), statusInstance.id])
        } else {
            response.status = SC_UNPROCESSABLE_ENTITY
            responseJson.errors = statusInstance.errors.fieldErrors.collectEntries {
                [(it.field): message(error: it)]
            }
        }

        render responseJson as JSON
    }

    def delete() {
        def statusInstance = Status.get(params.id)
        if (!statusInstance) {
            notFound params.id
            return
        }

        def responseJson = [:]
        try {
            statusInstance.delete(flush: true)
            responseJson.message = message(code: 'default.deleted.message', args: [message(code: 'status.label', default: 'Status'), params.id])
        } catch (DataIntegrityViolationException e) {
            response.status = SC_CONFLICT
            responseJson.message = message(code: 'default.not.deleted.message', args: [message(code: 'status.label', default: 'Status'), params.id])
        }
        render responseJson as JSON
    }

    private void notFound(id) {
        response.status = SC_NOT_FOUND
        def responseJson = [message: message(code: 'default.not.found.message', args: [message(code: 'status.label', default: 'Status'), params.id])]
        render responseJson as JSON
    }

    def display() {
        def statusInstance = Status.get(params.id)
        if (statusInstance) {
            render statusInstance as JSON
        } else {
            notFound params.id
        }
    }

    def inviteUser() {
        emailService.sendStatusLinkMail(params.id, params.email)
        Map data = [:]
        render data as JSON
    }

    def rateStatus() {
        Status status = Status.get(params.id);
        if (params.statusVote == '-1') {
            status.downVote = status.downVote += 1
        } else {
            status.upVote = status.upVote += 1
        }
        status.save(flush: true)
        Map data = [:]
        render data as JSON
    }

    def board() {
       List<Status>  allList = Status.list(sort: "upVote", order: "desc")
       List<Status>  recentList   = Status.list(sort: "lastUpdated", order: "desc",max: 10)
       Map myMap = [allList:allList,recentList:recentList]
       render myMap as JSON
    }

    def login() {
        Map data = [isUserExist: false]
        User user = User.findByUserNameAndPassword(params.username, params.password)
        if (user) {
            session.user = user
            data = [isUserExist: true, user: user]
        }
        render data as JSON
    }

    def logout(){
        Map data = [isUserLogin: false]
        session.invalidate()
        render data as JSON
    }

    def autoLoginByCookie(){
        Map data = [isUserExist: false]
        if(!session.user){
            User user = User.get(params.id)
            if (user){
                session.user = user
                data = [isUserExist: true]
            }
        }
        render data as JSON
    }
}
