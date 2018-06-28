def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.lib_name = ask("name: (${projectDir.name}) ", projectDir.name)
props.lib_group = ask("group: (org.example) ", 'org.example')
props.lib_version = ask("version: (0.1) ", '0.1')
props.lib_description = ask("description: ", '', 'lib_description')
props.lib_license = ask("license: (MIT) ", 'MIT', 'lib_license')

println ''
println '[Java]'
props.java_version = ask("version: (1.8) ", '1.8', 'java_version')

props.is_application = ask("application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.is_application) {
    props.lib_main_class = ask("application main class: ", '', 'lib_main_class')
}

println ''
println '[Gradle plugin]'
props.checkstyle_version = ask("checkstyle version: (7.1.2) ", '7.1.2')
props.findbugs_version = ask("findbugs version: (3.0.1) ", '3.0.1')
props.jacoco_version = ask("jacoco version: (0.7.5.201505241946) ", '0.7.5.201505241946')

processTemplates 'README.md', props
processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
