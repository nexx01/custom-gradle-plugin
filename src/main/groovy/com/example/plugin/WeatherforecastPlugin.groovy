package com.example.plugin

import org.gradle.api.*;

//heres my custom plugins that i need for this project


class WeatherforecastPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        2 - инжект расширения
//        def extension = project.extensions.create('weather', WeatherforecastExtension)

        project.task('forecast') {
            group = "Weather"
            doLast {
                println "---------------------------------------"
                println "Sunny 26 deg"
//                //        3 - использование расширения
//                println "Tomorow it will be " + extension.forecast
                println "---------------------------------------"
            }
        }
    }
}
