import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import uk.co.cacoethes.gradle.lazybones.LazybonesConventions

class VerifyTemplatePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val lazybones = project.extensions.getByType(LazybonesConventions::class)
        val lazybonesVerifier = project.extensions.create<VerifyTemplateExtension>("lazybonesVerifier", project.objects)
        project.tasks.addRule(VerifyTemplateRule(project, lazybones, lazybonesVerifier))
    }
}
