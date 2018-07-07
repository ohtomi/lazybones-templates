def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.artifact_name = ask("name: (${projectDir.name}) ", projectDir.name)
props.artifact_group = ask("group: (org.example) ", 'org.example')
props.artifact_version = ask("version: (0.1) ", '0.1')

println ''
println '[GitHub repository]'
props.repo_owner = ask("owner: (${System.getenv('USER')}) ", System.getenv('USER'))
props.repo_name = ask("name: (${projectDir.name}) ", projectDir.name)

println ''
println '[Java]'
props.java_version = ask("version: (1.8) ", '1.8', 'java_version')
props.is_application = ask("application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.is_application) {
    props.application_main_class_name = ask("application main class: ", '', 'lib_main_class')
}

println ''
println '[Gradle plugin]'
props.checkstyle_version = ask("Checkstyle version: (8.10.1) ", '8.10.1')
props.pmd_version = ask("PMD version: (6.5.0) ", '6.5.0')
props.spotbugs_version = ask("SpotBugs version: (3.1.5) ", '3.1.5')
props.jacoco_version = ask("JaCoCo version: (0.8.1) ", '0.8.1')
props.versions_version = ask("com.github.ben-manes.versions version: (0.20.0) ", '0.20.0')
props.audit_version = ask("net.ossindex.audit version: (0.3.19-beta) ", '0.3.19-beta')
props.bintray_version = ask("com.jfrog.bintray version: (1.8.3) ", '1.8.3')

processTemplates 'README.md', props
processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
