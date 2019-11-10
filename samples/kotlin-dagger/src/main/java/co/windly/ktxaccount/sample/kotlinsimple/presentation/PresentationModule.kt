package co.windly.ktxaccount.sample.kotlinsimple.presentation

import co.windly.ktxaccount.sample.kotlinsimple.presentation.main.MainModule
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [
  AndroidSupportInjectionModule::class,
  MainModule::class
])
class PresentationModule
