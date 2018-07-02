def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.artifact_name = ask("name: (${projectDir.name}) ", projectDir.name, 'artifact_name')
props.artifact_group = ask("group: (org.example) ", 'org.example', 'artifact_group')
props.artifact_version = ask("version: (0.1) ", '0.1', 'artifact_version')
props.artifact_description = ask("description: ", '', 'artifact_description')
props.artifact_license = ask("license: (MIT) ", 'MIT', 'artifact_license')

println ''
println '[Kotlin]'
props.kotlin_version = ask("version: (1.1.0) ", '1.1.0', 'kotlin_version')
props.is_application = ask("use application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.is_application) {
    props.application_main_class_name = ask("application main class: ", '', 'application_main_class_name')
}

println ''
println '[Gradle plugin]'
props.use_dokka = ask("use dokka? (yes) [yes/no] ", "yes") ==~ /(?i)y(es)?/

processTemplates 'README.md', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
