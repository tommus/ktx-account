package co.windly.ktxaccount.runtime.scheme

import android.accounts.Account
import android.content.Context
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * This scheme assumes ONE and ONLY account for given authenticator will ever exist.
 *
 * Client code should take all necessary effort that is required to make sure only
 * one account exist in particular time for given authenticator.
 */
abstract class SingleAccountScheme(context: Context) : BaseAccountScheme(context) {

  //region Account

  /**
   * Saves an account associated with provided authenticator type.
   */
  override fun saveAccount(name: String): Completable =
    clearExistingAccounts()
      .andThen(super.saveAccount(name))

  /**
   * Saves an account associated with provided authenticator type.
   */
  override fun saveAccount(name: String, password: String): Completable =
    clearExistingAccounts()
      .andThen(super.saveAccount(name, password))

  /**
   * Removes account associated with given email.
   */
  open fun removeAccount(): Completable =
    clearExistingAccounts()

  /**
   * Retrieves an account associated with provided authenticator and identified by
   * given name.
   *
   * @exception IllegalArgumentException when account identified by given name does
   * not exist.
   */
  protected fun retrieveAccount(): Account {

    // Retrieve account or an exception if account does not exist.
    return manager
      .getAccountsByType(provideAuthenticator())
      .firstOrNull()
      ?: throw IllegalArgumentException("Account does not exist.")
  }

  /**
   * Retrieves an account associated with provided authenticator and identified by
   * given name or null if the before mentioned account does not exist.
   */
  protected fun retrieveNullableAccount(): Account? {

    // Retrieve account or an exception if account does not exist.
    return manager
      .getAccountsByType(provideAuthenticator())
      .firstOrNull()
  }

  /**
   * Makes sure that no more than one and only account will ever exist for this
   * particular authenticator.
   */
  private fun clearExistingAccounts(): Completable =
    Completable
      .fromAction {

        // Clears all accounts associated with this authenticator.
        manager
          .getAccountsByType(provideAuthenticator())
          .forEach {

          @Suppress("DEPRECATION")
          manager.removeAccount(it, null, null)
        }
      }
      .subscribeOn(Schedulers.io())

  //endregion
}
