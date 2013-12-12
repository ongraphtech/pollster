import com.impl.pollster.*
class BootStrap {
    def bootstrapService

    def init = { servletContext ->
          bootstrapService.createUser()
          bootstrapService.createStatus()
          bootstrapService.createOptions()
          bootstrapService.createPollOpenion()
    }
    def destroy = {
    }
}
