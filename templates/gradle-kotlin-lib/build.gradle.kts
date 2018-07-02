import org.gradle.jvm.tasks.Jar
//import org.jetbrains.dokka.gradle.DokkaTask


buildscript {
//    val dokka_version = "0.9.14"

    repositories {
        jcenter()
    }

    dependencies {
//        classpath("org.jetbrains.dokka:dokka-gradle-plugin:\$dokka_version")
    }
}

plugins {
<% if (is_application) { %>\
    application
<% } %>\
    kotlin("jvm") version "${kotlin_version}"
//    id("org.jetbrains.dokka")
}


repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
}


base {
    archivesBaseName = project.properties["artifact_name"] as String
    group = project.properties["artifact_group"] as String
    version = project.properties["artifact_version"] as String
}

<% if (is_application) { %>\

application {
    applicationName = project.properties["application_name"] as String
    mainClassName = project.properties["application_main_class_name"] as String
}
<% } %>\


tasks.withType<Jar> {
    manifest.apply {
<% if (is_application) { %>\
        attributes["Main-Class"] = project.properties["application_main_class_name"] as String
<% } %>\
        attributes["Implementation-Title"] = project.properties["artifact_name"] as String
        attributes["Implementation-Version"] = project.properties["artifact_version"] as String
    }
}

//tasks.withType<DokkaTask> {
//    outputFormat = "html"
//    outputDirectory = "\${buildDir.absolutePath}/javadoc"
//    sourceDirs = files("src/main/kotlin")
//}
