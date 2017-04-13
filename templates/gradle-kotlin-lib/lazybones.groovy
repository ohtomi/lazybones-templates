def props = [:]
props.lib_name = ask("name: (${projectDir.name}) ", projectDir.name, 'lib_name')
props.lib_group = ask("group: (org.example) ", 'org.example', 'lib_group')
props.lib_version = ask("version: (0.1) ", '0.1', 'lib_version')

processTemplates 'README.md', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
