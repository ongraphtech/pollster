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
        Status status = new Status(request.JSON)
        status.postedBy = session?.user?.name
        Map responseJson = [:]
        if (status.save(flush: true)) {
            request.JSON.options.each {
                StatusOption statusOption = StatusOption.findByStatusAndStatusText(status, it.text.trim())
                if (!statusOption) {
                    new StatusOption(statusText: it.text, status: status).save(flush: true)
                }
            }
            response.status = SC_CREATED
            responseJson.id = status.id
            responseJson.message = message(code: 'default.created.message', args: [message(code: 'status.label', default: 'Status'), status.id])
        } else {
            response.status = SC_UNPROCESSABLE_ENTITY
            responseJson.errors = status.errors.fieldErrors.collectEntries {
                [(it.field): message(error: it)]
            }
        }
        render responseJson as JSON
    }

    def fetch() {
        Status status = Status.get(params.id)
        if (status) {
            render status as JSON
        } else {
            notFound params.id
        }
    }

    def update() {
        Status status = Status.get(params.id)
        if (!status) {
            notFound params.id
            return
        }
        Map responseJson = [:]
        if (request.JSON.version != null) {
            if (status.version > request.JSON.version) {
                response.status = SC_CONFLICT
                responseJson.message = message(code: 'default.optimistic.locking.failure',
                        args: [message(code: 'status.label', default: 'Status')],
                        default: 'Another user has updated this Status while you were editing')
                cache false
                render responseJson as JSON
                return
            }
        }

        status.properties = request.JSON

        if (status.save(flush: true)) {
            response.status = SC_OK
            responseJson.id = status.id
            responseJson.message = message(code: 'default.updated.message', args: [message(code: 'status.label', default: 'Status'), status.id])
        } else {
            response.status = SC_UNPROCESSABLE_ENTITY
            responseJson.errors = status.errors.fieldErrors.collectEntries {
                [(it.field): message(error: it)]
            }
        }

        render responseJson as JSON
    }

    def delete() {
        Status status = Status.get(params.id)
        if (!status) {
            notFound params.id
            return
        }

        Map responseJson = [:]
        try {
            status.delete(flush: true)
            responseJson.message = message(code: 'default.deleted.message', args: [message(code: 'status.label', default: 'Status'), params.id])
        } catch (DataIntegrityViolationException e) {
            response.status = SC_CONFLICT
            responseJson.message = message(code: 'default.not.deleted.message', args: [message(code: 'status.label', default: 'Status'), params.id])
        }
        render responseJson as JSON
    }

    private void notFound(id) {
        response.status = SC_NOT_FOUND
        Map responseJson = [message: message(code: 'default.not.found.message', args: [message(code: 'status.label', default: 'Status'), params.id])]
        render responseJson as JSON
    }

    def display() {
        Status status = Status.get(params.id)
        if (status) {
            List<Map> dtaList = []
            Map mapForchart = [:]
            status.statusOptions.sort {it.id}.each {StatusOption option ->
                mapForchart.put(option.statusText, PollOpinion.countByStatusOption(option))
                dtaList << mapForchart
                mapForchart = [:]
            }
            mapForchart = [status: status, dtaList: dtaList, statusOptions: status.statusOptions.sort {it.id}]
            JSON.use('deep') {
                render mapForchart as JSON
            }
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
        List<Status> allList = Status.list(sort: "upVote", order: "desc")
        List<Status> recentList = Status.list(sort: "lastUpdated", order: "desc", max: 10)
        Map myMap = [allList: allList, recentList: recentList]
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

    def logout() {
        Map data = [isUserLogin: false]
        session.invalidate()
        render data as JSON
    }

    def autoLoginByCookie() {
        Map data = [isUserExist: false]
        if (!session.user) {
            User user = User.get(params.id)
            if (user) {
                session.user = user
                data = [isUserExist: true]
            }
        }
        render data as JSON
    }

    def lockOption() {
        Map data = [message: "You have successfully locked your choice"]
        StatusOption statusOption = StatusOption.get(params.optionId)
        Status status = Status.get(params.id)
        PollOpinion pollOpinion = PollOpinion.findByUserAndStatus(session.user, status)
        if (pollOpinion) {
            pollOpinion.statusOption = statusOption
            data = [message: "You have successfully updated your choice"]
        }
        else {
            pollOpinion = new PollOpinion(user: session.user, status: status, statusOption: statusOption)
        }
        pollOpinion.save(flush: true)
        render data as JSON
    }

    def pollForChart() {
        List<Map> dtaList = []
        Map mapForchart = [:]
        Status status = Status.get(params.id)
        status.statusOptions.sort {it.id}.each {StatusOption option ->
            mapForchart.put(option.statusText, PollOpinion.countByStatusOption(option))
            dtaList << mapForchart
            mapForchart = [:]
        }
        render dtaList as JSON
    }


    def addCustomOption() {
        String optionText = params.optionText.trim()
        Status status = Status.get(params.id)
        StatusOption statusOption = StatusOption.findByStatusAndStatusText(status, optionText)
        if (!statusOption) {
            new StatusOption(statusText: optionText, status: status).save(flush: true)
        }
        status.refresh()
        render status.statusOptions.sort {it.id} as JSON
    }
}
