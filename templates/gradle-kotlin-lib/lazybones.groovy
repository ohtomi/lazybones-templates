def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.artifact_name = ask("name: (${projectDir.name}) ", projectDir.name, 'artifact_name')
props.artifact_group = ask("group: (org.example) ", 'org.example', 'artifact_group')

println ''
println '[GitHub repository]'
props.repo_owner = ask("owner: (${System.getenv('USER')}) ", System.getenv('USER'))
props.repo_name = ask("name: (${projectDir.name}) ", projectDir.name)

println ''
println '[Kotlin]'
props.is_application = ask("application? (no) [yes/no] ", "no") ==~ /(?i)y(es)?/

props.copyright_name = System.getenv('GITHUB_COPYRIGHT_NAME') ?: 'COPYRIGHT_NAME'
props.copyright_year = Calendar.getInstance().get(Calendar.YEAR)

processTemplates 'README.md', props
processTemplates 'LICENSE', props
processTemplates 'settings.gradle.kts', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

def gitignore = new File(projectDir.getAbsolutePath() + '/gitignore')
if (gitignore.exists()) {
    gitignore.renameTo(projectDir.getAbsolutePath() + '/.gitignore')
}
