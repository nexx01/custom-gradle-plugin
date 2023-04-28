package com.example.plugin;


import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WeatherforecastPluginTest {

    @Test
    void test() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(WeatherforecastPlugin.class);
        Assertions.assertNotNull(project.getTasksByName("forecast",true));
    }

}
