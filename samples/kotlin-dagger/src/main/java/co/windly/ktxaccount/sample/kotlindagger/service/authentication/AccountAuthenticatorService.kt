package co.windly.ktxaccount.sample.kotlindagger.service.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AccountAuthenticatorService : Service() {

  //region Binding

  override fun onBind(intent: Intent?): IBinder? =
    AccountAuthenticator(this).iBinder

  //endregion
}
