import org.jetbrains.dokka.gradle.DokkaTask


plugins {
<% if (is_application) { %>\
    application
<% } %>\
    kotlin("jvm") version "${kotlin_version}"
    id("org.jetbrains.dokka") version "0.9.17"
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC7-3"
}


repositories {
    jcenter()
}

val ktlint by configurations.creating

dependencies {
    compile(kotlin("stdlib"))
    ktlint("com.github.shyiko:ktlint:0.24.0")
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

// https://github.com/Kotlin/dokka
tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "\${buildDir.absolutePath}/javadoc"
    sourceDirs = files("src/main/kotlin")
}

// https://ktlint.github.io
// https://github.com/shyiko/ktlint
task<JavaExec>("ktlint") {
    val ktlint by configurations

    group = "verification"
    description = "Check Kotlin code style."
    isIgnoreExitValue = true
    classpath = ktlint
    main = "com.github.shyiko.ktlint.Main"
    args(listOf(
        "src/main/kotlin/**/*.kt",
        "--reporter=plain",
        "--reporter=checkstyle,output=\${buildDir}/reports/ktlint/ktlintMain.xml"
    ))
}
tasks.getByName("check").dependsOn("ktlint")


// https://arturbosch.github.io/detekt
// https://github.com/arturbosch/detekt
// https://github.com/arturbosch/detekt/blob/RC7-3/docs/pages/gettingstarted/kotlindsl.md
detekt {
    version = "1.0.0.RC7-3"
    profile("main", Action {
        input = "src/main/kotlin"
        filters = ".*/resources/.*,.*/build/.*"
    })
}
