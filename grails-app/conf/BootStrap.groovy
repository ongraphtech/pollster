import com.impl.pollster.*
class BootStrap {
    def bootstrapService

    def init = { servletContext ->
          bootstrapService.createUser()
          bootstrapService.createStatus()
    }
    def destroy = {
    }
}
