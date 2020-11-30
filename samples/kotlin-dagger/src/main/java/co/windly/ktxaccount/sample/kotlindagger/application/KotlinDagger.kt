package co.windly.ktxaccount.sample.kotlindagger.application

import android.app.Application
import co.windly.ktxaccount.sample.kotlindagger.service.authentication.AccountAuthenticator
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class KotlinDagger : Application(), ApplicationComponent.ComponentProvider, HasAndroidInjector {

  //region Component

  override lateinit var applicationComponent: ApplicationComponent

  //endregion

  //region Injector

  @Inject
  lateinit var androidInjector: DispatchingAndroidInjector<Any>

  override fun androidInjector(): AndroidInjector<Any> =
    androidInjector

  //endregion

  //region Lifecycle

  override fun onCreate() {
    super.onCreate()

    // Initialize dependency graph.
    initializeDependencyInjection()

    // Register Account listener
    registerAccountListener()
  }

  //endregion

  //region Dependency Injection

  private fun initializeDependencyInjection() {

    // Initialize application component.
    applicationComponent = DaggerApplicationComponent.builder()
      .application(this)
      .build()
    applicationComponent.inject(this)
  }

  //endregion

  //region Account

  val accountAuthenticator = AccountAuthenticator(this)

  private fun registerAccountListener() = accountAuthenticator.registerListener()

  //endregion

}
