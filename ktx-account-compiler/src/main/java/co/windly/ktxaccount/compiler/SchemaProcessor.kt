package co.windly.ktxaccount.compiler

import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.BASE_PACKAGE_PATH
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MAJOR_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MICRO_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MINOR_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.ProcessorConfiguration
import freemarker.template.Configuration
import freemarker.template.Version
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion.RELEASE_8
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
  value = [
    "co.windly.ktxaccount.annotation.AccountScheme",
    "co.windly.ktxaccount.annotation.DefaultBoolean",
    "co.windly.ktxaccount.annotation.DefaultFloat",
    "co.windly.ktxaccount.annotation.DefaultInt",
    "co.windly.ktxaccount.annotation.DefaultLong",
    "co.windly.ktxaccount.annotation.DefaultString",
    "co.windly.ktxaccount.annotation.Reactive"
  ]
)
@SupportedOptions(value = [ProcessorConfiguration.OPTION_KAPT_KOTLIN])
@SupportedSourceVersion(value = RELEASE_8)
class SchemaProcessor : AbstractProcessor() {

  //region Annotation Processor Configuration

  internal object ProcessorConfiguration {

    const val OPTION_KAPT_KOTLIN = "kapt.kotlin.generated"

    const val SRC_KAPT_KOTLIN = "kaptKotlin"

    const val SRC_KAPT = "kapt"
  }

  // TODO:

  //endregion

  //region Process

  override fun process(annotations: MutableSet<out TypeElement>?,
    environment: RoundEnvironment): Boolean {

    // Print a message that annotation processor started.
    processingEnv.messager.noteMessage { "Ktx Account Annotation Processor had started." }

    // Stop processing in case if there are no annotations declared.
    if (annotations == null) {
      return true
    }

    // Check whether Kotlin files target directory is accessible.
    val kaptTargetDirectory = processingEnv.options[ProcessorConfiguration.OPTION_KAPT_KOTLIN]
      ?: run {

        // Log an error message.
        processingEnv.messager.errorMessage { "Cannot access Kotlin files target directory." }

        // Stop processing in case if an error occurred.
        return true
      }

    annotations.forEach { annotation ->

      // Do nothing if incorrect annotated class has been passed to processor.
      if(annotation.qualifiedName.contentEquals("co.windly.ktxaccount.annotation.AccountScheme")) {
        return@forEach
      }

      // TODO:
    }

    // Print a message that annotation processor stopped.
    processingEnv
      .messager.noteMessage { "Ktx Account Annotation Processor had finished it's work." }

    return true
  }

  //endregion

  //region Template Processor Configuration

  private object FreemarkerConfiguration {

    const val MAJOR_VERSION = 2

    const val MINOR_VERSION = 3

    const val MICRO_VERSION = 28

    const val BASE_PACKAGE_PATH = ""
  }

  private object FreemarkerTemplate {

    // TODO:
  }

  private val freemarkerVersion: Version
    by lazy { Version(MAJOR_VERSION, MINOR_VERSION, MICRO_VERSION) }

  private val freemarkerConfiguration: Configuration
    by lazy {
      Configuration(freemarkerVersion)
        .also { it.setClassForTemplateLoading(javaClass, BASE_PACKAGE_PATH) }
    }

  //endregion
}
