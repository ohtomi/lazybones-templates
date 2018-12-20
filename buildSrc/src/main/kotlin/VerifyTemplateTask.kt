import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

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

            // TODO clean destBaseDir before running lazybones
            try {
                item.steps.forEach {
                    val commands = it.commands(templateName, templateVersion, index, item, project)
                    val directory = it.directory(templateName, templateVersion, index, item, project)
                    val timeout = it.timeout(templateName, templateVersion, index, item, project)
                    val returnCode = runExternalProcess(commands, directory, timeout, TimeUnit.SECONDS)
                    if (returnCode != 0) {
                        println("$SCREAM_EMOJI  [${it.name}] Failed. Something wrong.")
                        return@forEachIndexed
                    }
                    println("$THUMBS_UP_EMOJI  [${it.name}] Done.")
                }
                println("$CHEQUERED_FLAG_EMOJI  Finished.")
                success++
            } catch (e: IOException) {
                println("\n$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            } catch (e: InterruptedException) {
                println("\n$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            }
        }

        if (success < testCases.size) {
            throw TaskExecutionException(this, IllegalStateException("Template $templateName:$templateVersion has some errors."))
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    fun runExternalProcess(commands: Array<String>, directory: File, timeout: Long, unit: TimeUnit): Int = ProcessBuilder().run {
        command(*commands)
        directory(directory)
        start().run {
            waitFor(timeout, unit)
            if (isAlive) {
                destroyForcibly()
                waitFor(1, TimeUnit.SECONDS)
            }
            exitValue()
        }
    }
}

val LABEL_EMOJI = String(Character.toChars(0x1F3F7))

val WALKING_EMOJI = String(Character.toChars(0x1F6B6))

val SCREAM_EMOJI = String(Character.toChars(0x1F631))

val CHEQUERED_FLAG_EMOJI = String(Character.toChars(0x1F3C1))

val THUMBS_UP_EMOJI = String(Character.toChars(0x1F44D))
