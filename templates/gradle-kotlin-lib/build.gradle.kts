import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask


buildscript {
    extra["kotlin_version"] = "${kotlin_version}"
    extra["dokka_version"] = "0.9.14"

    repositories {
        gradleScriptKotlin()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:\${extra["dokka_version"]}")
    }
}

apply {
<% if (is_application) { %>\
    plugin("application")
<% } %>\
    plugin("kotlin")
    plugin("org.jetbrains.dokka")
}


repositories {
    gradleScriptKotlin()
}

dependencies {
    compile(kotlinModule("stdlib", extra["kotlin_version"] as String))
}


archivesBaseName = project.properties["artifact_name"] as String
group = project.properties["artifact_group"] as String
version = project.properties["artifact_version"] as String

<% if (is_application) { %>\

configure<ApplicationPluginConvention> {
    applicationName = project.properties["application_name"] as String
    mainClassName = project.properties["application_main_class_name"] as String
}
<% } %>\


tasks.withType<Jar> {
    manifest.apply {
<% if (is_application) { %>\
        attributes["Main-Class"] = project.properties["application_main_class_name"]
<% } %>\
        attributes["Implementation-Title"] = project.properties["artifact_name"]
        attributes["Implementation-Version"] = project.properties["artifact_version"]
    }
}

tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "\${buildDir.absolutePath}/javadoc"
    sourceDirs = files("src/main/kotlin")
}
