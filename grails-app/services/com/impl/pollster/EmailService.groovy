package com.impl.pollster

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.web.context.request.RequestContextHolder

class EmailService {

    void sendStatusLinkMail(String id, String email) {
        String body = "Please click the link :- http://localhost:8080/pollster/status/index#/display/"+ id
        try {
            sendJMSMessage("sendMail",
                    [to: email ,
                            subject: "Invitation to vote Status",
                            body: body])
        }
        catch (Exception e) {
            log.error e.message
        }

    }

}