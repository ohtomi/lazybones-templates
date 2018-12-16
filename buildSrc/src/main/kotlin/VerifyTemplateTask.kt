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

    @TaskAction
    fun verify() {
        val process = ProcessBuilder().command("lazybones", "list", "--cached").start()
        process.waitFor(5, TimeUnit.SECONDS)
        println(process.inputStream.readAllBytes().toString(StandardCharsets.UTF_8))
    }
}
