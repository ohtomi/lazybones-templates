def props = [:]

println 'Press ^C at any time to quit.'

println ''
println '[Maven artifact]'
props.artifact_name = ask("name: (${projectDir.name}) ", projectDir.name, 'artifact_name')
props.artifact_group = ask('group: (org.example) ', 'org.example', 'artifact_group')

println ''
println '[GitHub repository]'
props.repo_owner = ask("owner: (${System.getenv('USER')}) ", System.getenv('USER'), 'repo_owner')
props.repo_name = ask("name: (${projectDir.name}) ", projectDir.name, 'repo_name')

println ''
println '[App or Lib]'
props.is_application = ask('application? (no) [yes/no] ', 'no') ==~ /(?i)y(es)?/

props.copyright_name = System.getenv('GITHUB_COPYRIGHT_NAME') ?: 'COPYRIGHT_NAME'
props.copyright_year = Calendar.getInstance().get(Calendar.YEAR)

processTemplates 'README.md', props
processTemplates 'LICENSE', props
processTemplates 'settings.gradle.kts', props
processTemplates 'build.gradle.kts', props
processTemplates 'gradle.properties', props

[new File("${projectDir.getAbsolutePath()}/gitignore"),
 new File("${projectDir.getAbsolutePath()}/buildSrc/gitignore")].each { gitignore ->
    if (gitignore.exists()) {
        gitignore.renameTo("${gitignore.getParent()}/.gitignore")
    }
}
