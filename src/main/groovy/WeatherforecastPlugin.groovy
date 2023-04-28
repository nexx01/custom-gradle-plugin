import org.gradle.api.Plugin
import org.gradle.plugins.ide.eclipse.model.Project

//heres my custom plugins that i need for this project
class WeatherforecastPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        2 - инжект расширения
        def extension = project.extensions.create('weather', WeatherforecastExtension)

        project.task('weatherTomorrow') {
            group = "Weather"
            doLast {
                println "---------------------------------------"
                println "Tomorow it will be sunny and 25 deg. centigrade"
                //        3 - использование расширения
                println "Tomorow it will be " + extension.forecast
                println "---------------------------------------"
            }
        }
    }
}
