def props = [:]
props.lib_name = ask("name: (${projectDir.name}) ", projectDir.name)
props.lib_group = ask("group: (org.example) ", 'org.example')
props.lib_version = ask("version: (0.1) ", '0.1')

processTemplates 'README.md', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.name + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.name + '/.gitignore')
}
