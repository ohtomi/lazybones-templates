import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.kotlin.jvm").version(Versions.org_jetbrains_kotlin)
    id("org.jetbrains.dokka").version(Versions.org_jetbrains_dokka_gradle_plugin)
    id("io.gitlab.arturbosch.detekt").version(Versions.io_gitlab_arturbosch_detekt)
    id("net.ossindex.audit").version(Versions.net_ossindex_audit_gradle_plugin)
    id("jmfayard.github.io.gradle-kotlin-dsl-libs").version(Versions.jmfayard_github_io_gradle_kotlin_dsl_libs_gradle_plugin)
<% if (is_application) { %>\
    application
<% } %>\
    id("org.ajoberstar.reckon").version(Versions.org_ajoberstar_reckon_gradle_plugin)
    id("com.jfrog.bintray").version(Versions.com_jfrog_bintray_gradle_plugin)
    `maven-publish`
}

repositories {
    jcenter()
}

val ktlint by configurations.creating

dependencies {
    implementation(Libs.kotlin_stdlib_jdk8)

    testImplementation(Libs.kotlin_test)
    testImplementation(Libs.kotlin_test_junit)

    ktlint(Libs.ktlint)
    detektPlugins(Libs.detekt_formatting)
}


// https://github.com/ajoberstar/reckon
reckon {
    scopeFromProp()
    stageFromProp("beta", "rc", "final")
}

val props = Props(project)

val bintrayDryRun = true
val bintrayPublish = true
val bintrayOverride = false


val jar by tasks.existing(Jar::class) {
    manifest(closureOf<Manifest> {
        attributes(mapOf(
<% if (is_application) { %>\
                "Main-Class" to props.manifestMainClass,
<% } %>\
                "Implementation-Title" to props.manifestImplementationTitle,
                "Implementation-Version" to props.manifestImplementationVersion
        ))
    })
}

// https://github.com/Kotlin/dokka
val dokka by tasks.existing(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "\${buildDir}/docs/dokka"
    jdkVersion = 8
}

// https://ktlint.github.io
// https://github.com/shyiko/ktlint
val ktlintCheck by tasks.creating(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = configurations["ktlint"]
    main = "com.github.shyiko.ktlint.Main"
    args = listOf(
            "src/main/kotlin/**/*.kt",
            "--reporter=plain",
            "--reporter=checkstyle,output=\${buildDir}/reports/ktlint/ktlintMain.xml"
    )
    isIgnoreExitValue = true
}

// https://arturbosch.github.io/detekt
// https://github.com/arturbosch/detekt
// https://github.com/arturbosch/detekt/blob/1.0.0-RC11/docs/pages/gettingstarted/kotlindsl.md
detekt {
    toolVersion = Versions.io_gitlab_arturbosch_detekt
    input = files("src/main/kotlin")
    filters = ".*/resources/.*,.*/build/.*"
}

val check by tasks.existing {
    dependsOn(ktlintCheck, tasks["detekt"])
}

// https://github.com/OSSIndex/ossindex-gradle-plugin
audit {
    failOnError = false
}

val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

val javadocJar by tasks.creating(Jar::class) {
    from(dokka.get().outputDirectory)
    classifier = "javadoc"
    dependsOn(dokka)
}
<% if (is_application) { %>\

application {
    mainClassName = props.applicationMainClassName
}
<% } %>\

// https://github.com/bintray/gradle-bintray-plugin
val publicationName = "jcenterPublications"

publishing {
    publications.create(publicationName, MavenPublication::class) {
        from(components["java"])
        artifact(sourcesJar)
        artifact(javadocJar)
        groupId = props.artifactGroup
        artifactId = props.artifactName
        version = props.artifactVersion
        pom.withXml {
            asNode().apply {
                appendNode("name", props.packageName)
                appendNode("description", props.packageDescription)
                appendNode("url", props.packageWebsiteUrl)
                appendNode("licenses")
                        .appendNode("license").apply {
                            appendNode("name", props.packageLicenseName)
                            appendNode("url", props.packageLicenseUrl)
                        }
                appendNode("developers")
                        .appendNode("developer").apply {
                            appendNode("id", props.developerId)
                            appendNode("name", props.developerName)
                            appendNode("email", props.developerEmail)
                        }
                appendNode("scm").appendNode("url", props.packageVcsUrl)
            }
        }
    }
}

bintray {
    user = props.bintrayUsername
    key = props.bintrayApiKey
    setPublications(publicationName)
    dryRun = bintrayDryRun
    publish = bintrayPublish
    override = bintrayOverride
    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = props.packageName
        userOrg = props.bintrayUsername
        websiteUrl = props.packageWebsiteUrl
        issueTrackerUrl = props.packageIssueTrackerUrl
        vcsUrl = props.packageVcsUrl
        setLicenses(props.packageLicenseName)
        setLabels(*props.packageLabels)
        publicDownloadNumbers = true
        version(closureOf<BintrayExtension.VersionConfig> {
            name = props.packageVersionName
            released = props.packageVersionReleased
            vcsTag = props.packageVersionVcsTag
        })
    })
}
