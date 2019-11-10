package co.windly.ktxaccount.runtime.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle

class SimpleAccountAuthenticator(private val context: Context) :
  AbstractAccountAuthenticator(context) {

  //region Account Manager

  // TODO:

  //endregion

  //region Application Token

  override fun getAuthTokenLabel(p0: String?): String {
    TODO("not implemented")
  }

  override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?,
    p3: Bundle?): Bundle {
    TODO("not implemented")
  }

  //endregion

  //region Account

  override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?,
    p3: Array<out String>?, p4: Bundle?): Bundle {
    TODO("not implemented")
  }

  //endregion

  //region Credentials

  override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?,
    p2: Bundle?): Bundle {
    TODO("not implemented")
  }

  override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?,
    p3: Bundle?): Bundle {
    TODO("not implemented")
  }

  //endregion

  //region Properties

  override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
    TODO("not implemented")
  }

  //endregion

  //region Features

  override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?,
    p2: Array<out String>?): Bundle {
    TODO("not implemented")
  }

  //endregion
}
