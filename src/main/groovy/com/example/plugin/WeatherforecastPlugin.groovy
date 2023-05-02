package com.example.plugin

import org.gradle.api.*
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import org.gradle.testing.jacoco.tasks.JacocoReport

import static java.util.stream.Collectors.toList
import static java.util.stream.Collectors.toList;

//heres my custom plugins that i need for this project


class WeatherforecastPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        //        2 - инжект расширения
        def extension = project.extensions.create('contractTestProperty', ExternalClassesContractTestExtension)

        project.afterEvaluate {
            def f = project.getTasksByName("jacocoContractTestReport", false)

            f.forEach {
                println("+++++++++++++=Start plugin")
                println("+++++++++++++=Test For Grep")
                println("+++++++++++++=Test For Grep")
                println("+++++++++++++=getExtension " + extension.forecast)
                println("+++++++++++++=getExtension " + extension.message)
                println("+++++++++++++=extension.externalClassesMap  -> " + extension.externalClasses)
                println("+++++++++++++=extension.externalClassesMap  -> " + extension.enternalClasses)
                def it1 = (JacocoReport) it
                it.dependsOn("contractTest")

                println("+++++++++++++=")
                println("+++++++++++++=")
                // очистка предыдущих классов из sourceSets.main
                it1.classDirectories.from.clear()
                it1.sourceDirectories.from.clear()

                includeClassesIntoContractTestReport(it1, extension.externalClasses, project)
                includeClassesIntoContractTestReport(it1, extension.enternalClasses, project)


                it1.reports.html.required.value(true)
                it1.reports.html.outputLocation.fileValue(new File("${project.buildDir}/reports/jacoco/contractTest/html"))

                it1.reports.xml.required.value(true)
                it1.reports.xml.outputLocation.fileValue(new File("${project.buildDir}/reports/jacoco/contractTest/jacocoContractTestReport.xml"))
                println("+++++++++++++=End plugin")
            }
        }
    }

    def includeClassesIntoContractTestReport(JacocoReport jacocoReport, HashMap<String, Set<String>> stringSetHashMap, Project project) {
        println("+++++++++++++=includeClassesIntoContractTestReport")
        externalClasses_CoverageInclusion(jacocoReport, stringSetHashMap, project)
        enternalClasses_CoverageInclusion(jacocoReport, stringSetHashMap, project)
    }

    def externalClasses_CoverageInclusion(JacocoReport jacocoReport, HashMap<String, Set<String>> stringSetHashMap, Project project) {
        println("+++++++++++++=externalClasses_CoverageInclusion")
        def s = stringSetHashMap.entrySet()
                .stream()
                .map { externalClassesInput(it.key, it.value, project) }
                .collect(toList())

        println("+++++++++++++=before includeClasses " + s.toString())
        includeClasses(jacocoReport, s as List<Map<Object, Object>>)
    }

    def enternalClasses_CoverageInclusion(JacocoReport jacocoReport, HashMap<String, Set<String>> stringSetHashMap, Project project) {
        println("+++++++++++++=enternalClasses_CoverageInclusion")
        def s = stringSetHashMap.entrySet()
                .stream()
                .map { externalClassesInput(it.key, it.value, project) }
                .collect(toList())

        println("+++++++++++++=before includeClasses " + s.toString())
        includeClasses(jacocoReport, s as List<Map<Object, Object>>)
    }

/*
    КЛАССЫ MAIN МОДУЛЯ
*/

    def mainClassesInput(List<String> jarRelativePaths,Project project) {
        def classDirectories = project.fileTree("${project.buildDir.toString()}/classes/java/main").matching { patternFilterable ->
            jarRelativePaths.each { filePath ->
                patternFilterable.include("${filePath}.class")
            }
        }
        def sourcesTargetDir = "${project.buildDir.toString()}/sources" as Object
        project.copy {
            it.from project.sourceSets.main.allJava.getSourceDirectories().getAsFileTree().matching { patternFilterable ->
                jarRelativePaths.each { filePath ->
                    patternFilterable.include("${filePath}.java")
                }
            }
            it.eachFile { it.relativePath = sourceFileRelativePath(it) }
            it.includeEmptyDirs false
            it.into sourcesTargetDir
        }
        return [classDirectories: classDirectories, sourceDirectories: "${sourcesTargetDir}/src/main/java"]
    }

/*
    КЛАССЫ ЛОКАЛЬНЫХ МОДУЛЕЙ
*/

    def subprojectClassesInput(String moduleName, List<String> jarRelativePaths, Project project) {
        def classDirectories = subprojectClassesLocation(moduleName, jarRelativePaths,project)
        def sourcesDirectories = subprojectSourcesLocation(moduleName, jarRelativePaths,project)
        return [classDirectories: classDirectories, sourceDirectories: sourcesDirectories]
    }

    def subprojectSourcesLocation(String moduleName, List<String> jarRelativePaths, Project project) {
        def subprojectSourceSet = subprojectMainSourceSet(moduleName,project)
        def sourcesTargetDir = "${project.buildDir.toString()}/tmp/expandedArchives/${moduleName}-sources" as Object
        project.copy {
            it.from subprojectSourceSet.allJava.getSourceDirectories().getAsFileTree().matching { patternFilterable ->
                jarRelativePaths.each { filePath ->
                    patternFilterable.include("${filePath}.java")
                }
            }
            it.eachFile { it.relativePath = sourceFileRelativePath(it) }
            it.includeEmptyDirs false
            it.into sourcesTargetDir
        }
        return "${sourcesTargetDir}/src/main/java"
    }

    def subprojectClassesLocation(String moduleName, List<String> jarRelativePaths, Project project) {
        def subprojectSourceSet = subprojectMainSourceSet(moduleName,project)
        def classesTargetDir = "${project.buildDir.toString()}/tmp/expandedArchives/${moduleName}-classes"
        project.copy {
            it.from subprojectSourceSet.output.classesDirs.getAsFileTree().matching { patternFilterable ->
                jarRelativePaths.each { filePath ->
                    patternFilterable.include("${filePath}.class")
                }
            }
            it.eachFile { it.relativePath = classFileRelativePath(it) }
            it.includeEmptyDirs false
            it.into classesTargetDir
        }
        return classesTargetDir
    }

    def subprojectMainSourceSet(String subProjectName, Project project) {
        return project.subprojects.stream()
                .filter { it.name == subProjectName }
                .findAny()
                .map { it.extensions.getByType(JavaPluginExtension) }
                .map { it.sourceSets.getByName('main') }
                .get()
    }


/*
    КЛАССЫ БИБЛИОТЕК И ВНЕШНИХ МОДУЛЕЙ
*/
    def externalClassesInput(String moduleName, Set<String> jarRelativePaths, Project project) {
        def classDirectories = externalClassesLocation(moduleName, jarRelativePaths, project)
        def sourcesDirectories = externalSourcesLocation(moduleName, jarRelativePaths, project)
        return [classDirectories: classDirectories, sourceDirectories: sourcesDirectories]
    }

    def externalClassesLocation(String moduleName, Set<String> jarRelativePaths, Project project) {
        def moduleClassesJar = externalClassesJar(moduleName, project)
        println("+++++++++++++=before  form classesTargetDir in externalClassesLocation ")

        def classesTargetDir = project.buildDir.toString() + "/tmp/expandedArchives/${moduleName}-classes" as Object
        println("+++++++++++++=before  // для сохранения структуры classes/java/main  " + classesTargetDir)

        // для сохранения структуры classes/java/main
        project.copy {
            it.from project.zipTree(moduleClassesJar).matching { patternFilterable ->
                jarRelativePaths.each { filePath ->
                    patternFilterable.include("${filePath}.class")
                }
            }
            it.eachFile { it.relativePath = classFileRelativePath(it) }
            it.includeEmptyDirs false
            it.into classesTargetDir
        }
        return classesTargetDir
    }

    def externalSourcesLocation(String moduleName, Set<String> jarRelativePaths, Project project) {
        def moduleSourcesJar = externalSourcesJar(moduleName, project)
        def sourcesTargetDir = project.buildDir.toString() + "/tmp/expandedArchives/${moduleName}-sources" as Object
        // для Jacoco обязательно нужна структура src/main/java, в противном случае исходники он не подтянет
        project.copy {
            it.from project.zipTree(moduleSourcesJar).matching { patternFilterable ->
                jarRelativePaths.each { filePath ->
                    patternFilterable.include("${filePath}.java")
                }
            }
            it.eachFile {
                it.relativePath = sourceFileRelativePath(it)
            }
            it.includeEmptyDirs false
            it.into sourcesTargetDir
        }
        return "${sourcesTargetDir}/src/main/java" as Object
    }

    static def externalClassesJar(String moduleName, Project project) {
        println("+++++++++++++=before  // возврат самого jar архива")
        // возврат самого jar архива

        def d = project.sourceSets.main.compileClasspath.filter { it.getName().contains(moduleName) }.getSingleFile()
        println("+++++++++++++=before  // возврат самого jar архива" + d)

        return d;
    }

    def externalSourcesJar(String moduleName, Project project) {
        def componentIdentifier = project.configurations.compileClasspath.incoming.resolutionResult.allDependencies.stream()
                .filter { (it.requested.moduleIdentifier as String).contains(moduleName) }
                .findAny()
                .map { (it as ResolvedDependencyResult).selected.id }
                .get()


        def artifactResolution = project.dependencies.createArtifactResolutionQuery()
                .forComponents(componentIdentifier)
                .withArtifacts(JvmLibrary, SourcesArtifact)
                .execute()

        println("+++++++++++++=before  // artifactResolution.resolvedComponents")
        def f = artifactResolution.resolvedComponents
                .stream()
                .findFirst()
                .flatMap { it.getArtifacts(SourcesArtifact).stream().findFirst() }
                .map { ((ResolvedArtifactResult) it).file }
                .get()
        println("+++++++++++++=before  // return artifactResolution")
        return f;
    }

    /*
    УТИЛИТЫ
    */

    static def includeClasses(JacocoReport jr, List<Map<Object, Object>> locations) {
        println("+++++++++++++= static def includeClasses(JacocoReport jr, List<Map<Object, Object>> locations) {")
        locations.each {
            println("+++++++++++++= includeClasses" + it.classDirectories)
            println("+++++++++++++= includeClasses" + it.sourceDirectories)
            jr.classDirectories.from += it.classDirectories
            jr.sourceDirectories.from += it.sourceDirectories
        }
    }

    static def sourceFileRelativePath(FileCopyDetails file) {
        def pathSegments = ['src', 'main', 'java', *file.relativePath.segments] as String[]
        return new RelativePath(true, pathSegments)
    }

    static def classFileRelativePath(FileCopyDetails file) {
        def pathSegments = ['classes', 'java', 'main', *file.relativePath.segments] as String[]
        return new RelativePath(true, pathSegments)
    }
}
