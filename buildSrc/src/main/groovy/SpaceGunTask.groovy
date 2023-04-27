import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SpaceGunTask extends DefaultTask{

    @TaskAction
    def fire() {
        println(" Somthing fiiiiirere!!!!")
    }
}
