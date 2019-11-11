package co.windly.ktxaccount.annotation

/**
 * Indicates a wrapper methods for given Boolean property should be generated.
 * <p>
 * One can configure what default value will be used in case if given property was
 * not set previously,
 */
@Target(AnnotationTarget.FIELD)
annotation class DefaultBoolean(

  /**
   * The default value for given Boolean property.
   * @return the default value for given Boolean property
   */
  val value: Boolean
)
