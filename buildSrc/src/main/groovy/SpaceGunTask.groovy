import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SpaceGunTask extends DefaultTask{

    @TaskAction
    def fire() {
        def spacefile= project.file('spaceoutput/spacegun.txt')
        spacefile.parentFile.mkdirs()
        spacefile.write(" Somthing fiiiiirere!!!!")

        println("-----------------")
        println("Look in to the file friend: "+spacefile.name)
        println("-----------------")

    }
}
