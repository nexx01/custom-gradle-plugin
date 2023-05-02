package com.example.plugin


import java.util.stream.Collectors

//Extension
class ExternalClassesContractTestExtension {    //1 - class для расширения
    String forecast = 'NA'

    String message = 'NA'

    Map<String, Set<String>> externalClasses = new HashMap<>()

    Map<String, Set<String>> enternalClasses = new HashMap<>()

    def addExternalClasses(String moduleName, String... jarRelativePaths) {
        addExternalClasses(moduleName, Arrays.stream(jarRelativePaths).collect(Collectors.toList()))
    }

    def addExternalClasses(String moduleName, List<String> jarRelativePaths) {
        if(externalClasses.containsKey(moduleName)){
            externalClasses.get(moduleName).addAll(jarRelativePaths)
        } else {
            def paths = new HashSet<String>()
            paths.addAll(jarRelativePaths)
            externalClasses.put(moduleName,paths)
        }
    }

    def addEnternalClasses(String moduleName, String... jarRelativePaths) {
        addEnternalClassesMap(moduleName, Arrays.stream(jarRelativePaths).collect(Collectors.toList()))
    }

    def addEnternalClasses(String moduleName, List<String> jarRelativePaths) {
        if(enternalClasses.containsKey(moduleName)){
            enternalClasses.get(moduleName).addAll(jarRelativePaths)
        } else {
            def paths = new HashSet<String>()
            paths.addAll(jarRelativePaths)
            enternalClasses.put(moduleName,paths)
        }
    }

}

