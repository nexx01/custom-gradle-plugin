import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class SpaceGunTask extends DefaultTask{

    @OutputDirectory
    def myFile ='filenamenotset.txt'

    @TaskAction
    def fire() {
        def spacefile= project.file('spaceoutput/spacegun.txt')
        def spacefile1= project.file('spaceoutput/'+ myFile)
        spacefile.parentFile.mkdirs()
        spacefile.write(" Somthing fiiiiirere!!!!")

        spacefile1.parentFile.mkdirs()
        spacefile1.write(" Somthing fiiiiirere!!!!")

        println("-----------------")
        println("Look in to the file friend: "+spacefile.name)
        println("-----------------")
        println("-----------------")
        println("Look in to the file friend: "+spacefile1.path)
        println("-----------------")

    }
}
