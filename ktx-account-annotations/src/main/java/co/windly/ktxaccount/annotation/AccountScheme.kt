package co.windly.ktxaccount.annotation

import androidx.annotation.StringDef
import co.windly.ktxaccount.annotation.AccountScheme.Mode.Companion.MULTIPLE
import co.windly.ktxaccount.annotation.AccountScheme.Mode.Companion.SINGLE
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Marks a given scheme class to be a subjection of ktx-account
 * annotation processing.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class AccountScheme(

  /**
   * Configures library to handle one and only account associated with
   * this particular authenticator.
   *
   * By default this option is turned off.
   */
  @Mode
  val value: String = MULTIPLE,

  /**
   * Alias for value property.
   */
  @Mode
  val mode: String = MULTIPLE
) {

  /**
   * Provides all available values for library working mode configuration.
   */
  @Retention(SOURCE)
  @StringDef(value = [
    SINGLE,
    MULTIPLE
  ])
  annotation class Mode {

    companion object {

      /**
       * Configures library to handle only single account for given
       * authenticator.
       */
      const val SINGLE = "single"

      /**
       * Configures library to handle multiple accounts for given
       * authenticator.
       */
      const val MULTIPLE = "multiple"
    }
  }
}
