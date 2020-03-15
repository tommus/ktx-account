package co.windly.ktxaccount.sample.kotlindagger.utility.manager

import android.content.Context
import co.windly.ktxaccount.sample.kotlindagger.utility.account.UserScheme
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.annotations.SchedulerSupport.IO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SchedulerSupport(value = IO)
class AccountUtilityManager @Inject constructor(context: Context) :
  UserScheme(context) {

  //region Authenticator

  override fun provideAuthenticator(): String =
    "co.windly.ktxaccount.sample.kotlindagger"

  //endregion
}
