package co.windly.ktxaccount.annotation

/**
 * Indicates a wrapper methods for given String property should be generated.
 * <p>
 * One can configure what default value will be used in case if given property was
 * not set previously,
 */
@Target(AnnotationTarget.FIELD)
annotation class DefaultString(

  /**
   * The default value for given String property.
   * @return the default value for given String property
   */
  val value: String
)
