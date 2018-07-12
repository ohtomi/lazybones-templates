import java.util.Date
import java.text.SimpleDateFormat
import org.jetbrains.dokka.gradle.DokkaTask
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask


plugins {
<% if (is_application) { %>\
    application
<% } %>\
    kotlin("jvm") version "${kotlin_version}"
    id("org.jetbrains.dokka") version "${dokka_version}"
    id("io.gitlab.arturbosch.detekt") version "${detekt_version}"
    id("com.github.ben-manes.versions") version "${versions_version}"
    id("net.ossindex.audit") version "${audit_version}"
    id("com.jfrog.bintray") version "${bintray_version}"
    `maven-publish`
}


repositories {
    jcenter()
}

val ktlint by configurations.creating

dependencies {
    compile(kotlin("stdlib"))
    ktlint("com.github.shyiko:ktlint:${ktlint_version}")
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


tasks.withType<Test> {
    maxParallelForks = 1
    setForkEvery(100)
    minHeapSize = "128m"
    maxHeapSize = "128m"
    jvmArgs = listOf("-XX:+UseG1GC")
}

tasks.withType<Jar> {
    manifest.apply {
<% if (is_application) { %>\
        attributes["Main-Class"] = application_main_class_name
<% } %>\
        attributes["Implementation-Title"] = artifact_name
        attributes["Implementation-Version"] = artifact_version
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    val dokka by tasks

    classifier = "javadoc"
    from("\${buildDir}/docs/javadoc")
}

// https://github.com/Kotlin/dokka
tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "\${buildDir}/docs/avadoc"
}

// https://ktlint.github.io
// https://github.com/shyiko/ktlint
val ktlint by tasks.creating(JavaExec::class) {
    val ktlint by configurations

    group = "verification"
    description = "Check Kotlin code style."
    isIgnoreExitValue = true
    classpath = ktlint
    main = "com.github.shyiko.ktlint.Main"
    args = listOf(
        "src/main/kotlin/**/*.kt",
        "--reporter=plain",
        "--reporter=checkstyle,output=\${buildDir}/reports/ktlint/ktlintMain.xml"
    )
}
tasks.getByName("check").dependsOn("ktlint")

// https://arturbosch.github.io/detekt
// https://github.com/arturbosch/detekt
// https://github.com/arturbosch/detekt/blob/RC7-3/docs/pages/gettingstarted/kotlindsl.md
detekt {
    version = "${detekt_version}"
    profile("main", Action {
        input = "src/main/kotlin"
        filters = ".*/resources/.*,.*/build/.*"
    })
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
    revision = "release"
}

// https://github.com/OSSIndex/ossindex-gradle-plugin
audit {
    failOnError = false
}

val bintray_username: String by project
val bintray_api_key: String by project
val developer_id: String by project
val developer_name: String by project
val developer_email: String by project

// https://github.com/bintray/gradle-bintray-plugin
bintray {
    user = bintray_username
    key = bintray_api_key
    setPublications("jcenterPublication")
    dryRun = true
    publish = true
    override = false
    pkg.apply {
        repo = "generic"
        name = artifact_name
        userOrg = bintray_username
        setLicenses("MIT")
        vcsUrl = "https://github.com/${repo_owner}/${repo_name}.git"
        version.apply {
            name = artifact_version
            desc = artifact_version
            released = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(Date())
            // setAttributes(mapOf("" to ""))
            // gpg.sign = true
        }
    }
}

fun org.gradle.api.publish.maven.MavenPom.buildXml() = withXml {
    asNode().apply {
        appendNode("description", "TODO")
        appendNode("name", artifact_name)
        appendNode("url", "https://github.com/${repo_owner}/${repo_name}")

        appendNode("licenses").appendNode("license").apply {
            appendNode("name", "MIT")
            appendNode("url", "https://${repo_owner}.mit-license.org")
            appendNode("distribution", "repo")
        }
        appendNode("developers").appendNode("developer").apply {
            appendNode("id", developer_id)
            appendNode("name", developer_name)
            appendNode("email", developer_email)
        }
        appendNode("scm").apply {
            appendNode("url", "https://github.com/${repo_owner}/${repo_name}")
        }
    }
}

publishing {
    (publications) {
        "jcenterPublication"(MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
            groupId    = artifact_group
            artifactId = artifact_name
            version    = artifact_version
            pom.buildXml()
        }
    }
}
