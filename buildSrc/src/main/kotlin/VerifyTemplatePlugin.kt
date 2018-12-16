import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import uk.co.cacoethes.gradle.lazybones.LazybonesConventions

class VerifyTemplatePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.getByType(LazybonesConventions::class)
        project.tasks.addRule(VerifyTemplateRule(project, extension))
    }
}
