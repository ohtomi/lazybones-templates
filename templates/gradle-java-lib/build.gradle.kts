import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.jvm.tasks.Jar

plugins {
    java
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs").version(Versions.com_github_spotbugs_gradle_plugin)
    id("jacoco")
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

dependencies {
    implementation(Libs.guava)

    testImplementation(Libs.junit)
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

// https://docs.gradle.org/current/userguide/checkstyle_plugin.html
// https://checkstyle.org
// https://github.com/checkstyle/checkstyle
//checkstyle {}

// https://docs.gradle.org/current/userguide/pmd_plugin.html
// https://pmd.github.io
// https://github.com/pmd/pmd
// https://github.com/pmd/pmd/tree/pmd_releases/6.5.0/pmd-java/src/main/resources/rulesets/java
//pmd {}

// https://docs.gradle.org/current/userguide/findbugs_plugin.html
// https://spotbugs.github.io
// https://github.com/spotbugs/spotbugs
//spotbugs {}

// https://docs.gradle.org/current/userguide/jacoco_plugin.html
// https://www.jacoco.org
// https://github.com/jacoco/jacoco
//jacoco {}

// https://github.com/OSSIndex/ossindex-gradle-plugin
audit {
    failOnError = false
}

val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

val javadocJar by tasks.creating(Jar::class) {
    from(tasks["javadoc"].outputs)
    classifier = "javadoc"
    dependsOn("javadoc")
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
