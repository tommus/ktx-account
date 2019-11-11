package co.windly.ktxaccount.runtime.scheme

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber

abstract class BaseAccountScheme(private val context: Context) {

  //region Authenticator

  /**
   * Provides authenticator type.
   */
  abstract fun provideAuthenticator(): String

  //endregion

  //region Account Manager

  protected val manager: AccountManager
    by lazy { AccountManager.get(context) }

  //endregion

  //region Account

  /**
   * Saves an account associated with provided authenticator type.
   */
  open fun saveAccount(name: String): Completable =
    Completable
      .fromAction {

        // Create account.
        val account = Account(name, provideAuthenticator())

        // Save account.
        manager.addAccountExplicitly(account, null, null)
      }
      .subscribeOn(Schedulers.io())

  /**
   * Saves an account associated with provided authenticator type.
   */
  open fun saveAccount(name: String, password: String): Completable =
    Completable
      .fromAction {

        // Create account.
        val account = Account(name, provideAuthenticator())

        // Save account.
        manager.addAccountExplicitly(account, password, null)
      }
      .subscribeOn(Schedulers.io())

  /**
   * Removes account associated with given email.
   */
  open fun removeAccount(name: String): Completable =
    Completable
      .fromAction {

        // Retrieve account.
        val account = manager
          .getAccountsByType(provideAuthenticator())
          .first { it.name == name }

        // Remove account.
        @Suppress("DEPRECATION")
        manager.removeAccount(account, null, null)
      }
      .subscribeOn(Schedulers.io())

  /**
   * Retrieves an account associated with provided authenticator and identified by
   * given name.
   *
   * @exception IllegalArgumentException when account identified by given name does
   * not exist.
   */
  protected fun retrieveAccount(name: String): Account {

    // Retrieve account or an exception if account does not exist.
    return manager
      .getAccountsByType(provideAuthenticator())
      .firstOrNull { it.name == name }
      ?: throw IllegalArgumentException("Account does not exist.")
  }

  /**
   * Retrieves an account associated with provided authenticator and identified by
   * given name or null if the before mentioned account does not exist.
   */
  protected fun retrieveNullableAccount(name: String): Account? {

    // Retrieve account or an exception if account does not exist.
    return manager
      .getAccountsByType(provideAuthenticator())
      .firstOrNull { it.name == name }
  }

  //endregion
}
