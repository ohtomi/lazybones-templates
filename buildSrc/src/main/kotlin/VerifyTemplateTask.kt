import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

open class VerifyTemplateTask : DefaultTask() {

    @Input
    var templateName: String = ""

    @Input
    var templateVersion: String = ""

    @Input
    var destBaseDir: String = "."

    @Input
    var testCases: List<VerifyTemplateConventionItem> = emptyList()

    @TaskAction
    fun verify() {
        if (testCases.isEmpty()) {
            throw TaskExecutionException(this, IllegalStateException("No test cases for $templateName $templateVersion"))
        }

        // see http://oswald.hatenablog.com/entry/20100426/1272240082
        testCases.forEachIndexed { index: Int, item: VerifyTemplateConventionItem ->
            // TODO clean destBaseDir before running lazybones
            val destDir = "$destBaseDir/$index"
            val commands = arrayOf("lazybones", "create", templateName, templateVersion, destDir, *item.params)
            ProcessBuilder(*commands).run {
                try {
                    val process = start()
                    process.waitFor(3, TimeUnit.SECONDS)
                    if (process.isAlive) {
                        process.destroy()
                    }
                    println(process.errorStream.readAllBytes().toString(StandardCharsets.UTF_8))
                    // TODO close all streams of process
                } catch (e: IOException) {

                } catch (e: InterruptedException) {

                }
            }
        }
    }
}
