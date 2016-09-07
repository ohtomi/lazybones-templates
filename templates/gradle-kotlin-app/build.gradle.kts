import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask


buildscript {
    extra["kotlin_version"] = "1.0.3"
    extra["dokka_version"] = "0.9.9"

    repositories {
        gradleScriptKotlin()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${extra["dokka_version"]}")
    }
}

apply {
    plugin("application")
    plugin("kotlin")
    plugin("org.jetbrains.dokka")
}


repositories {
    gradleScriptKotlin()
}

dependencies {
    compile(kotlinModule("stdlib", extra["kotlin_version"] as String))
}


group = project.properties["app_group"] as String
version = project.properties["app_version"] as String

configure<ApplicationPluginConvention> {
    applicationName = project.properties["app_name"] as String
    mainClassName = project.properties["app_main_class"] as String
}


tasks.withType<Jar> {
    baseName = project.properties["app_name"] as String

    manifest.apply {
        attributes["Main-Class"] = project.properties["app_main_class"]
        attributes["Implementation-Title"] = project.properties["app_name"]
        attributes["Implementation-Version"] = project.properties["app_version"]
    }
}


tasks.withType<DokkaTask> {
    outputFormat = "javadoc"
    outputDirectory = "${buildDir.absolutePath}/javadoc"
    sourceDirs = files("src/main/kotlin")
}
