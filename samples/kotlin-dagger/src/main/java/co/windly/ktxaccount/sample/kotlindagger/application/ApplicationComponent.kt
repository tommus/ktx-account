package co.windly.ktxaccount.sample.kotlindagger.application

import co.windly.ktxaccount.sample.kotlindagger.presentation.PresentationModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    ApplicationModule::class,
    PresentationModule::class
  ]
)
interface ApplicationComponent {

  //region Injection

  fun inject(applicationModule: KotlinDagger)

  //endregion

  //region Component Builder

  @Component.Builder
  interface Builder {

    @BindsInstance
    fun application(application: KotlinDagger): Builder

    fun build(): ApplicationComponent
  }

  //endregion

  //region Component Provider

  interface ComponentProvider {

    val applicationComponent: ApplicationComponent
  }

  //endregion
}
