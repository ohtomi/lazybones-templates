import org.gradle.api.Project
import org.gradle.api.Rule
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import uk.co.cacoethes.gradle.lazybones.LazybonesConventions

class VerifyTemplateRule(val project: Project, val extension: LazybonesConventions) : Rule {

    val taskNameMatcher = """verifyTemplate([A-Z\-]\S+)""".toRegex()

    override fun apply(taskName: String) {
        val result = taskNameMatcher.find(taskName)
        if (result != null) {
            val camelCaseTmplName = result.groupValues[1]
            val pkgTask = project.tasks.getByName("installTemplate$camelCaseTmplName", Copy::class) ?: return
            project.tasks.create(taskName, VerifyTemplateTask::class).apply {
                templateName = TODO("camel case template name to kebab case")
                templateVersion = TODO("read VERSION file")

                dependsOn(pkgTask)
            }
        }
    }

    override fun getDescription(): String = "verifyTemplate<TpmlName> - Verifies the template in the directory matching the task name"

    override fun toString(): String = "Rule: $description"
}
