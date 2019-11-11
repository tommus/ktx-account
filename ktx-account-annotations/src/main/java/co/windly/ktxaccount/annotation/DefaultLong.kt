package co.windly.ktxaccount.annotation

/**
 * Indicates a wrapper methods for given Long property should be generated.
 * <p>
 * One can configure what default value will be used in case if given property was
 * not set previously,
 */
@Target(AnnotationTarget.FIELD)
annotation class DefaultLong(

  /**
   * The default value for given Long property.
   * @return the default value for given Long property
   */
  val value: Long
)
