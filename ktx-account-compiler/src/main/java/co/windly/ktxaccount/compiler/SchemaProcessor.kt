package co.windly.ktxaccount.compiler

import co.windly.ktxaccount.annotation.AccountScheme
import co.windly.ktxaccount.annotation.AccountScheme.Mode
import co.windly.ktxaccount.annotation.DefaultBoolean
import co.windly.ktxaccount.annotation.DefaultDouble
import co.windly.ktxaccount.annotation.DefaultFloat
import co.windly.ktxaccount.annotation.DefaultInt
import co.windly.ktxaccount.annotation.DefaultLong
import co.windly.ktxaccount.annotation.DefaultString
import co.windly.ktxaccount.annotation.Name
import co.windly.ktxaccount.annotation.Reactive
import co.windly.ktxaccount.compiler.PropertyType.BOOLEAN
import co.windly.ktxaccount.compiler.PropertyType.DOUBLE
import co.windly.ktxaccount.compiler.PropertyType.FLOAT
import co.windly.ktxaccount.compiler.PropertyType.INTEGER
import co.windly.ktxaccount.compiler.PropertyType.LONG
import co.windly.ktxaccount.compiler.PropertyType.STRING
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.BASE_PACKAGE_PATH
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MAJOR_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MICRO_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.FreemarkerConfiguration.MINOR_VERSION
import co.windly.ktxaccount.compiler.SchemaProcessor.ProcessorConfiguration
import freemarker.template.Configuration
import freemarker.template.Version
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.nio.charset.Charset
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion.RELEASE_8
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter

@SupportedAnnotationTypes(
  value = [
    "co.windly.ktxaccount.annotation.AccountScheme",
    "co.windly.ktxaccount.annotation.DefaultBoolean",
    "co.windly.ktxaccount.annotation.DefaultDouble",
    "co.windly.ktxaccount.annotation.DefaultFloat",
    "co.windly.ktxaccount.annotation.DefaultInt",
    "co.windly.ktxaccount.annotation.DefaultLong",
    "co.windly.ktxaccount.annotation.DefaultString",
    "co.windly.ktxaccount.annotation.Name",
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

  private object SuffixConfiguration {

    const val SCHEME = "Scheme"

    const val CONSTANTS = "Constants"
  }

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

    annotations.forEach annotations@{ annotation ->

      // Do nothing if incorrect annotated class has been passed to processor.
      if (!annotation.qualifiedName.contentEquals(
          "co.windly.ktxaccount.annotation.AccountScheme")) {
        return@annotations
      }

      // Iterate over all class elements.
      environment.getElementsAnnotatedWith(annotation).forEach { element ->

        // Retrieve a class meta information.
        val packageElement = element.enclosingElement as PackageElement
        val classComment = processingEnv.elementUtils.getDocComment(element)

        // Retrieve a class reactive meta information.
        val classReactive = element.getAnnotation(Reactive::class.java)
        val classEnableReactive = classReactive?.value ?: true
        val classDistinctUntilChanged = classReactive?.distinctUntilChanged ?: true

        // Prepare property descriptor collection.
        val descriptorList = mutableListOf<Property>()

        // Iterate over the fields of given class.
        ElementFilter.fieldsIn(element.enclosedElements).forEach variable@{ variableElement ->

          // Ignore constants.
          if (variableElement.modifiers.contains(Modifier.STATIC)) {
            return@variable
          }

          // Retrieve field type information.
          val fieldType = variableElement.asType()

          // Check whether given type can be handled by this library.
          val isSupportedType = PropertyType.isAllowedType(fieldType)

          // Close annotation processor with an error in case if there is unsupported field type.
          if (!isSupportedType) {

            // Print an error message.
            processingEnv.messager
              .errorMessage { "Processed class contains field type ($fieldType) which is not supported." }

            // Halt the annotation processor.
            return true
          }

          // Retrieve field meta information.
          val fieldName = variableElement.simpleName.toString()
          val fieldNameAnnotation = variableElement.getAnnotation(Name::class.java)
          val fieldComment = processingEnv.elementUtils.getDocComment(variableElement)

          // Prepare property meta information.
          val propertyName = getPropertyName(fieldName, fieldNameAnnotation)
          val propertyDefault = getPropertyDefault(variableElement, fieldType)

          // Retrieve reactive meta information for the single property.
          val variableReactive = variableElement.getAnnotation(Reactive::class.java)
          val enableReactive = variableReactive?.value ?: classEnableReactive ?: true
          val distinctUntilChanged = variableReactive?.distinctUntilChanged
            ?: classDistinctUntilChanged ?: true

          // Create a field property description.
          val descriptor = Property(
            fieldName = fieldName,
            propertyName = propertyName,
            type = PropertyType.from(fieldType),
            defaultValue = propertyDefault,
            comment = fieldComment,
            enableReactive = enableReactive,
            distinctUntilChanged = distinctUntilChanged
          )

          // Add descriptor to descriptor list.
          descriptorList += descriptor
        }

        // Retrieve a class account scheme annotation.
        val schemeAnnotation = element.getAnnotation(AccountScheme::class.java)
        val schemaMode = schemeAnnotation.mode

        // Validate schema mode.
        if (schemaMode !in listOf(Mode.SINGLE, Mode.MULTIPLE)) {

          // Print a message that incorrect schema mode has been selected.
          processingEnv
            .messager.noteMessage { "Incorrect schema mode has been selected." }

          // Halt the annotation processor.
          return true
        }

        // Prepare argument container.
        val arguments: MutableMap<String, Any?> = mutableMapOf()

        // Prepare class-level reactive definition.
        arguments["classEnableReactive"] = classEnableReactive
        arguments["schemaMode"] = schemaMode

        // Prepare class basics arguments.
        arguments["package"] = packageElement.qualifiedName
        arguments["comment"] = classComment

        // Prepare generated names arguments.
        arguments["schemeClassName"] = "${element.simpleName}${SuffixConfiguration.SCHEME}"
        arguments["constantsClassName"] = "${element.simpleName}${SuffixConfiguration.CONSTANTS}"

        // Prepare descriptor list.
        arguments["descriptorList"] = descriptorList

        // Make directory for generated files.
        val packageDirectory = File(
          kaptTargetDirectory.replace(ProcessorConfiguration.SRC_KAPT_KOTLIN,
            ProcessorConfiguration.SRC_KAPT),
          getPackagePath(packageElement))
          .also { it.mkdirs() }

        // Create scheme.
        File(packageDirectory,
          "${element.simpleName}${SuffixConfiguration.SCHEME}.kt").apply {
          writer(Charset.defaultCharset())
            .use { writer ->
              val template = freemarkerConfiguration.getTemplate(FreemarkerTemplate.SCHEME)
              template.process(arguments, writer)
            }
        }

        // Create constants file that uses property arguments.
        File(packageDirectory,
          "${element.simpleName}${SuffixConfiguration.CONSTANTS}.kt").apply {
          writer(Charset.defaultCharset())
            .use { writer ->
              val template = freemarkerConfiguration.getTemplate(FreemarkerTemplate.CONSTANTS)
              template.process(arguments, writer)
            }
        }
      }
    }

    // Print a message that annotation processor stopped.
    processingEnv
      .messager.noteMessage { "Ktx Account Annotation Processor had finished it's work." }

    return true
  }

  //endregion

  //region Path

  private fun getPackagePath(packageElement: PackageElement): String =
    packageElement.qualifiedName.toString().replace(".", "/")

  //endregion

  //region Properties

  private fun getPropertyName(fieldName: String, fieldNameAnnotation: Name?): String? =
    fieldNameAnnotation?.value ?: fieldName

  private fun getPropertyDefault(variableElement: VariableElement,
    fieldType: TypeMirror): String? {

    // Parse for Boolean.
    variableElement.getAnnotation(DefaultBoolean::class.java)?.let {
      return when (isAnnotationSupported(BOOLEAN, fieldType)) {
        false -> null
        true -> it.value.toString()
      }
    }

    // Parse for Float.
    variableElement.getAnnotation(DefaultFloat::class.java)?.let {
      return when (isAnnotationSupported(FLOAT, fieldType)) {
        false -> null
        true -> "${it.value}f"
      }
    }

    // Parse for Double.
    variableElement.getAnnotation(DefaultDouble::class.java)?.let {
      return when (isAnnotationSupported(DOUBLE, fieldType)) {
        false -> null
        true -> "${it.value}"
      }
    }

    // Parse for Int.
    variableElement.getAnnotation(DefaultInt::class.java)?.let {
      return when (isAnnotationSupported(INTEGER, fieldType)) {
        false -> null
        true -> "${it.value}"
      }
    }

    // Parse for Long.
    variableElement.getAnnotation(DefaultLong::class.java)?.let {
      return when (isAnnotationSupported(LONG, fieldType)) {
        false -> null
        true -> "${it.value}L"
      }
    }

    // Parse for String.
    variableElement.getAnnotation(DefaultString::class.java)?.let {
      return when (isAnnotationSupported(STRING, fieldType)) {
        false -> null
        true -> "\"${StringEscapeUtils.escapeJava(it.value)}\""
      }
    }

    // Return null value for unsupported types.
    return "null"
  }

  private fun isAnnotationSupported(propertyType: PropertyType, fieldType: TypeMirror): Boolean {

    if (propertyType.isCompatible(fieldType).not()) {

      // Print an error message.
      processingEnv.messager.errorMessage { "Processed class contains field type ($fieldType) which is not supported." }

      // Halt the annotation processor.
      return false
    }
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

    const val SCHEME = "scheme.ftl"

    const val CONSTANTS = "constants.ftl"
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
