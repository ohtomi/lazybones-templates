def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.lib_name = ask("name: (${projectDir.name}) ", projectDir.name, 'lib_name')
props.lib_group = ask("group: (org.example) ", 'org.example', 'lib_group')
props.lib_version = ask("version: (0.1) ", '0.1', 'lib_version')
props.lib_description = ask("description: ", '', 'lib_description')
props.lib_license = ask("license: (MIT) ", 'MIT', 'lib_license')

println ''
println '[Kotlin]'
props.kotlin_version = ask("version: (1.1.0) ", '1.1.0', 'kotlin_version')

println ''
println '[Gradle plugin]'
props.use_application = ask("use application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.use_application) {
    props.lib_main_class = ask("application main class: ", '', 'lib_main_class')
}
props.use_dokka = ask("use dokka? (yes) [yes/no] ", "yes") ==~ /(?i)y(es)?/

processTemplates 'README.md', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
