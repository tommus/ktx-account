package co.windly.ktxaccount.annotation

/**
 * Indicates a wrapper methods for given Double property should be generated.
 * <p>
 * One can configure what default value will be used in case if given property was
 * not set previously,
 */
@Target(AnnotationTarget.FIELD)
annotation class DefaultDouble(

  /**
   * The default value for given Double property.
   * @return the default value for given Double property
   */
  val value: Double
)
