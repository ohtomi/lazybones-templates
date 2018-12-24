import org.gradle.api.Project
import org.gradle.api.Rule
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByName
import uk.co.cacoethes.gradle.lazybones.LazybonesConventions
import uk.co.cacoethes.gradle.lazybones.TemplateConvention
import uk.co.cacoethes.gradle.util.NameConverter
import java.io.File

class VerifyTemplateRule(
        val project: Project,
        val lazybones: LazybonesConventions,
        val lazybonesVerifier: VerifyTemplateExtension
) : Rule {

    private val taskNameMatcher = """verifyTemplate([A-Z\-]\S+)""".toRegex()

    override fun apply(taskName: String) {
        val result = taskNameMatcher.find(taskName)
        if (result != null) {
            val camelCaseTmplName = result.groupValues[1]
            val hyphenatedTmplName = NameConverter.camelCaseToHyphenated(camelCaseTmplName)
            val templateConvention = findTemplateConvention(hyphenatedTmplName)
            val templateDir = findTemplateDir(hyphenatedTmplName) ?: return
            val versionText = templateConvention?.version ?: project.file("$templateDir/VERSION").readText().trim()
            val installTask = findInstallTask(camelCaseTmplName) ?: return
            val extensionItems = lazybonesVerifier.templates.filter { it.name == hyphenatedTmplName }
            project.tasks.create(taskName, VerifyTemplateTask::class).apply {
                templateName = hyphenatedTmplName
                templateVersion = versionText
                testCases = extensionItems

                dependsOn(installTask)
            }
            val packageTask = findPackageTask(camelCaseTmplName) ?: return
            project.tasks.create("ignoreTemplate$camelCaseTmplName", IgnoreTemplateTask::class, templateDir).apply {
                packageTask.dependsOn(this)
            }
        }
    }

    private fun findTemplateConvention(hyphenatedTmplName: String): TemplateConvention? =
            lazybones.templateConventions.find { it.name == hyphenatedTmplName }

    private fun findTemplateDir(hyphenatedTmplName: String): File? =
            lazybones.templateDirs.files.find { it.name == hyphenatedTmplName }

    private fun findInstallTask(camelCaseTmplName: String): Copy? =
            project.tasks.getByName("installTemplate$camelCaseTmplName", Copy::class)

    private fun findPackageTask(camelCaseTmplName: String): Zip? =
            project.tasks.getByName("packageTemplate$camelCaseTmplName", Zip::class)

    override fun getDescription(): String =
            "verifyTemplate<TpmlName> - Verifies the template in the directory matching the task name"

    override fun toString(): String = "Rule: $description"
}
