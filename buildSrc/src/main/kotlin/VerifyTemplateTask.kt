import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.IOException

open class VerifyTemplateTask : DefaultTask() {

    @Input
    var templateName: String = ""

    @Input
    var templateVersion: String = ""

    @Input
    var testCases: List<VerifyTemplateConventionItem> = emptyList()

    @TaskAction
    fun verify() {
        if (testCases.isEmpty()) {
            throw TaskExecutionException(this, IllegalStateException("No test cases for $templateName $templateVersion"))
        }

        println("$LABEL_EMOJI  version: $templateVersion")
        var success = 0

        testCases.forEachIndexed { index: Int, item: VerifyTemplateConventionItem ->
            println("\n${index + 1} of ${testCases.size} $WALKING_EMOJI ...")

            try {
                val workDir = project.workDir(templateName, index)
                workDir.deleteRecursively()

                item.steps.forEach {
                    val returnCode = it.execute(templateName, templateVersion, index, item, project)
                    if (returnCode != 0) {
                        println("$SCREAM_EMOJI  [${it.name}] Failed. Something wrong. rc: $returnCode")
                        return@forEachIndexed
                    }
                    println("$THUMBS_UP_EMOJI  [${it.name}] Done.")
                }
                println("$CHEQUERED_FLAG_EMOJI  Finished.")
                success++
            } catch (e: IOException) {
                println("$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            } catch (e: InterruptedException) {
                println("$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            }
        }

        if (success < testCases.size) {
            throw TaskExecutionException(this, IllegalStateException("Template $templateName:$templateVersion has some errors."))
        }
    }
}

val LABEL_EMOJI = String(Character.toChars(0x1F3F7))

val WALKING_EMOJI = String(Character.toChars(0x1F6B6))

val SCREAM_EMOJI = String(Character.toChars(0x1F631))

val CHEQUERED_FLAG_EMOJI = String(Character.toChars(0x1F3C1))

val THUMBS_UP_EMOJI = String(Character.toChars(0x1F44D))
