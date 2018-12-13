import org.gradle.api.Project

class Props(project: Project) {
    val artifactName = project.name
    val artifactGroup = project.group.toString()
    val artifactVersion = project.version.toString()

    val applicationMainClassName = project.extIfExists("application_main_class_name", "")

    val manifestMainClass = project.extIfExists("manifest_main_class", applicationMainClassName)
    val manifestImplementationTitle = project.extIfExists("manifest_implementation_title", "$artifactGroup:$artifactName")
    val manifestImplementationVersion = project.extIfExists("manifest_implementation_version", artifactVersion)

    val bintrayUsername = project.extIfExists("bintray_username", System.getenv("BINTRAY_USER") ?: "")
    val bintrayApiKey = project.extIfExists("bintray_api_key", System.getenv("BINTRAY_KEY") ?: "")
    val developerId = project.extIfExists("developer_id", System.getenv("DEVELOPER_ID") ?: "")
    val developerName = project.extIfExists("developer_name", System.getenv("DEVELOPER_NAME") ?: "")
    val developerEmail = project.extIfExists("developer_email", System.getenv("DEVELOPER_EMAIL") ?: "")

    val packageName = project.extIfExists("package_name", "$artifactGroup:$artifactName")
    val packageDescription = project.extIfExists("package_description", "")
    val packageWebsiteUrl = project.extIfExists("package_website_url", "https://github.com/user/repo")
    val packageIssueTrackerUrl = project.extIfExists("package_issue_tracker_url", "$packageWebsiteUrl/issues")
    val packageVcsUrl = project.extIfExists("package_vcs_url", "$packageWebsiteUrl.git")
    val packageLicenseName = project.extIfExists("package_license_name", "MIT")
    val packageLicenseUrl = project.extIfExists("package_license_url", "https://opensource.org/licenses/MIT")
    val packageLabels = project.extIfExists("package_labels", "").split(",").toTypedArray()
    val packageVersionName = project.extIfExists("package_version_name", artifactVersion)
    val packageVersionReleased: String = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(java.util.Date())
    val packageVersionVcsTag = project.extIfExists("package_version_vcs_tag", artifactVersion)
}

fun Project.extIfExists(name: String, defaultValue: String): String = if (project.extensions.extraProperties.has(name)) {
    project.extensions.extraProperties[name] as String
} else {
    defaultValue
}
