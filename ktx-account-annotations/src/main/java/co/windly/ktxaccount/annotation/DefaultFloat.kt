package co.windly.ktxaccount.annotation

/**
 * Indicates a wrapper methods for given Float property should be generated.
 * <p>
 * One can configure what default value will be used in case if given property was
 * not set previously,
 */
@Target(AnnotationTarget.FIELD)
annotation class DefaultFloat(

  /**
   * The default value for given Float property.
   * @return the default value for given Float property
   */
  val value: Float
)
