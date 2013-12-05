package com.impl.pollster

class SendMailService {
    boolean transactional = false

    //Tell the JMS plugin that this is a message endpoint
    static expose = ['jms']
    static destination = "sendMail"

    //The Mail plugin service
    def mailService

    def onMessage(emailMessage) {
        try {
            mailService.sendMail {
                if (emailMessage.to != null) {
                    to(emailMessage?.to)
                }
                if (emailMessage.bcc != null) {
                    bcc(emailMessage?.bcc)
                }
                subject(emailMessage.subject)
                //body(view: emailMessage.view, model: emailMessage.model)
                body(emailMessage.body)  // changed for making body without view
            }
            log.info("Email sent to....${emailMessage.to}")
        }
        catch (Exception e) {
            log.error("Failed to send email ${emailMessage}", e)
        }
        finally {
            log.info("Email may be sent....")
        }
        //Return null to avoid poison messages
        return null
    }
}
