package co.windly.ktxaccount.sample.kotlindagger.service.authentication

import android.app.Activity
import android.content.Context
import co.windly.ktxaccount.runtime.service.SimpleAccountAuthenticator
import co.windly.ktxaccount.sample.kotlindagger.presentation.main.MainActivity

class AccountAuthenticator(context: Context) : SimpleAccountAuthenticator(context) {

  //region Authenticator

  override fun provideAuthenticator(): String =
    "co.windly.ktxaccount.sample.kotlindagger"

  //endregion

  //region Start Destination

  override fun provideStartDestination(): Class<out Activity> =
    MainActivity::class.java

  //endregion

}
