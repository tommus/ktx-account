package co.windly.ktxaccount.annotation

/**
 * Allows to specify a property name for the field.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FIELD)
annotation class Name(

  /**
   * Name of property.
   *
   * @return Name of the property.
   */
  val value: String
)
