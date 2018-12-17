import org.gradle.api.Project
import org.gradle.api.Rule
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import uk.co.cacoethes.gradle.lazybones.LazybonesConventions
import uk.co.cacoethes.gradle.util.NameConverter

class VerifyTemplateRule(
        val project: Project,
        val lazybones: LazybonesConventions,
        val lazybonesVerifier: VerifyTemplateExtension
) : Rule {

    val taskNameMatcher = """verifyTemplate([A-Z\-]\S+)""".toRegex()

    override fun apply(taskName: String) {
        val result = taskNameMatcher.find(taskName)
        if (result != null) {
            val camelCaseTmplName = result.groupValues[1]
            val hyphenatedTmplName = NameConverter.camelCaseToHyphenated(camelCaseTmplName)
            val installTask = project.tasks.getByName("installTemplate$camelCaseTmplName", Copy::class) ?: return
            val extensionItems = lazybonesVerifier.collection.filter { it.name == hyphenatedTmplName }
            project.tasks.create(taskName, VerifyTemplateTask::class).apply {
                templateName = hyphenatedTmplName
                templateVersion = "1.5.1" // TODO read templates/$templateName/VERSION file
                destDir = "${project.buildDir}/lazybones-projects/$hyphenatedTmplName"
                testCases = extensionItems

                dependsOn(installTask)
            }
        }
    }

    override fun getDescription(): String = "verifyTemplate<TpmlName> - Verifies the template in the directory matching the task name"

    override fun toString(): String = "Rule: $description"
}
