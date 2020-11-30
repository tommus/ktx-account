package co.windly.ktxaccount.runtime.scheme

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import co.windly.ktxaccount.runtime.service.SimpleAccountAuthenticator
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS

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
   * Observe that for this authenticator exist any account
   */
  val observeAccountExistence = SimpleAccountAuthenticator
    .availableAccounts
    .toFlowable(LATEST)
    .throttleLatest(200, MILLISECONDS)
    .map {

      // Check that any account match to our authenticator
      it.any { account -> account.type == provideAuthenticator() }
    }
    .distinctUntilChanged()

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
