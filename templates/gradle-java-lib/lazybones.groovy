def props = [:]
props.lib_name = ask("name: (${projectDir.name}) ", projectDir.name)
props.lib_group = ask("group: (org.example) ", 'org.example')
props.lib_version = ask("version: (0.1) ", '0.1')

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

def gitignore = new File(projectDir.name + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.name + '/.gitignore')
}
