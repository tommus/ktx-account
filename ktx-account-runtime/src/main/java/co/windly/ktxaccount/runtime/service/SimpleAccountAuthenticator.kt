package co.windly.ktxaccount.runtime.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

abstract class SimpleAccountAuthenticator(private val context: Context) :
  AbstractAccountAuthenticator(context) {

  //region Authenticator

  abstract fun provideAuthenticator(): String

  //endregion

  //region Start Destination

  abstract fun provideStartDestination(): Class<out Activity>

  //endregion

  //region Account Manager

  private val manager: AccountManager
    by lazy { AccountManager.get(context) }

  //endregion

  //region Application Token

  override fun getAuthTokenLabel(p0: String?): String =
    ""

  override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?,
    p3: Bundle?): Bundle = Bundle()

  //endregion

  //region Account

  override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?,
    p3: Array<out String>?, p4: Bundle?): Bundle? {

    // Do nothing if account already added.
    if (manager.getAccountsByType(provideAuthenticator()).isNotEmpty()) {
      return null
    }

    // Prepare bundle to open login fragment.
    val intent = Intent(context, provideStartDestination())

    // Return bundle for authentication intent.
    return Bundle().apply { putParcelable(AccountManager.KEY_INTENT, intent) }
  }

  //endregion

  //region Credentials

  override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?,
    p2: Bundle?): Bundle = Bundle()

  override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?,
    p3: Bundle?): Bundle = Bundle()

  //endregion

  //region Properties

  override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle =
    Bundle()

  //endregion

  //region Features

  override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?,
    p2: Array<out String>?): Bundle =
    Bundle().apply { putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false) }

  //endregion
}
