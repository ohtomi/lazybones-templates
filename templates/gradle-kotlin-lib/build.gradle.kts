import org.jetbrains.dokka.gradle.DokkaTask


plugins {
<% if (is_application) { %>\
    application
<% } %>\
    kotlin("jvm") version "${kotlin_version}"
    id("org.jetbrains.dokka") version "0.9.17"
}


repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
}


val artifact_name: String by project
val artifact_group: String by project
val artifact_version: String by project

base {
    archivesBaseName = artifact_name
    group = artifact_group
    version = artifact_version
}

<% if (is_application) { %>\

val application_name: String by project
val application_main_class_name: String by project

application {
    applicationName = application_name
    mainClassName = application_main_class_name
}
<% } %>\


tasks.withType<Jar> {
    manifest.apply {
<% if (is_application) { %>\
        attributes["Main-Class"] = application_main_class_name
<% } %>\
        attributes["Implementation-Title"] = artifact_name
        attributes["Implementation-Version"] = artifact_version
    }
}

tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "\${buildDir.absolutePath}/javadoc"
    sourceDirs = files("src/main/kotlin")
}
