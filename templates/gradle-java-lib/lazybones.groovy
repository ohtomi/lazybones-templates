def props = [:]
props.lib_name = ask("Define value for 'name' [java-lib]: ", 'java-lib', 'lib_name')
props.lib_group = ask("Define value for 'group' [org.example]: ", 'org.example', 'lib_group')
props.lib_version = ask("Define value for 'version' [0.1]: ", '0.1', 'lib_version')

processTemplates 'README.md', props
processTemplates 'gradle.properties', props
