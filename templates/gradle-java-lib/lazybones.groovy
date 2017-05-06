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

println ''
println '[Gradle plugin]'

props.use_application = ask("use application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.use_application) {
    props.lib_main_class = ask("application main class: ", '', 'lib_main_class')
}

props.use_checkstyle = ask("use checkstyle: (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.use_checkstyle) {
  props.checkstyle_version = ask("checkstyle version: (7.1.2) ", '7.1.2')
} else {
  checkstyle_xml = new File(projectDir.name + '/checkstyle.xml')
  if (checkstyle_xml.exists()) {
    checkstyle_xml.delete()
  }
}

props.use_findbugs = ask("use findbugs: (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.use_findbugs) {
  props.findbugs_version = ask("findbugs version: (3.0.1) ", '3.0.1')
}

props.use_jacoco = ask("use jacoco: (no) [yes/no] ", "no") ==~ /(?i)y(es)?/
if (props.use_jacoco) {
  props.jacoco_version = ask("jacoco version: (0.7.5.201505241946) ", '0.7.5.201505241946')
}

processTemplates 'README.md', props
processTemplates 'build.gradle', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
