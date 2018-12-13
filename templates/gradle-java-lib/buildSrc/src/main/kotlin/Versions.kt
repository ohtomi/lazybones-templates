import kotlin.String

/**
 * Find which updates are available by running
 *     `$ ./gradlew syncLibs`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val com_github_spotbugs_gradle_plugin: String = "1.6.6" 

    const val guava: String = "27.0.1-jre" 

    const val com_jfrog_bintray_gradle_plugin: String = "1.8.4" 

    const val jmfayard_github_io_gradle_kotlin_dsl_libs_gradle_plugin: String = "0.2.6" 

    const val junit: String = "4.12" 

    const val net_ossindex_audit_gradle_plugin: String = "0.3.21" 

    const val org_ajoberstar_reckon_gradle_plugin: String = "0.9.0" 

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "5.0"

        const val currentVersion: String = "5.0"

        const val nightlyVersion: String = "5.2-20181212000032+0000"

        const val releaseCandidate: String = ""
    }
}
