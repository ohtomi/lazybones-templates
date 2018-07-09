def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.artifact_name = ask("name: (${projectDir.name}) ", projectDir.name, 'artifact_name')
props.artifact_group = ask("group: (org.example) ", 'org.example', 'artifact_group')
props.artifact_version = ask("version: (0.1) ", '0.1', 'artifact_version')

println ''
println '[GitHub repository]'
props.repo_owner = ask("owner: (${System.getenv('USER')}) ", System.getenv('USER'))
props.repo_name = ask("name: (${projectDir.name}) ", projectDir.name)

println ''
println '[Kotlin]'
// see. https://youtrack.jetbrains.com/issue/KT-21303
props.kotlin_version = ask("version: (1.2.30) ", '1.2.30', 'kotlin_version')
props.is_application = ask("application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.is_application) {
    props.application_main_class_name = ask("application main class: ", '', 'application_main_class_name')
}

println ''
println '[Gradle plugin]'
props.dokka_version = ask("dokka version: (0.9.17) ", '0.9.17')
props.ktlint_version = ask("ktlint version: (0.24.0) ", '0.24.0')
props.detekt_version = ask("io.gitlab.arturbosch.detekt version: (1.0.0.RC7-3) ", '1.0.0.RC7-3')
props.versions_version = ask("com.github.ben-manes.versions version: (0.20.0) ", '0.20.0')
props.audit_version = ask("net.ossindex.audit version: (0.3.19-beta) ", '0.3.19-beta')
props.bintray_version = ask("com.jfrog.bintray version: (1.8.3) ", '1.8.3')

processTemplates 'README.md', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
