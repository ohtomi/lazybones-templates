def props = [:]
props.app_name = ask("Define value for 'name' [java-app]: ", 'java-app', 'app_name')
props.app_group = ask("Define value for 'group' [org.example]: ", 'org.example', 'app_group')
props.app_version = ask("Define value for 'version' [0.1]: ", '0.1', 'app_version')
props.app_main_class = ask("Define value for 'main class' [org.example.Main]: ", 'org.example.Main', 'app_main_class')

processTemplates 'README.md', props
processTemplates 'gradle.properties', props
