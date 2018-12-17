import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

open class VerifyTemplateTask : DefaultTask() {

    @Input
    var templateName: String = ""

    @Input
    var templateVersion: String = ""

    @Input
    var destDir: String = "."

    @Input
    var testCases: List<VerifyTemplateConventionItem> = emptyList()

    @TaskAction
    fun verify() {
        if (testCases.isEmpty()) {
            logger.error("No test cases for $templateName $templateVersion")
            return
        }

        testCases.forEachIndexed { index: Int, item: VerifyTemplateConventionItem ->
            // TODO clean destDir before running lazybones
            val process = ProcessBuilder()
                    .command("lazybones", "create", templateName, templateVersion, "$destDir/$index", *item.params)
                    .start()
            process.waitFor(3, TimeUnit.SECONDS)
            if (process.isAlive) {
                process.destroy()
            }
            println(process.errorStream.readAllBytes().toString(StandardCharsets.UTF_8))
        }
    }
}
