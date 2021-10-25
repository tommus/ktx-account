package co.windly.ktxaccount.runtime.scheme

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

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
}
