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
props.is_application = ask("use application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.is_application) {
    props.application_main_class_name = ask("application main class: ", '', 'application_main_class_name')
}

// println ''
// println '[Gradle plugin]'

processTemplates 'README.md', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
