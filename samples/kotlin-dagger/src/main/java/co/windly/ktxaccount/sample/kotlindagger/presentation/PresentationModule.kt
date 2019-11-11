package co.windly.ktxaccount.sample.kotlindagger.presentation

import co.windly.ktxaccount.sample.kotlindagger.presentation.main.MainModule
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [
  AndroidSupportInjectionModule::class,
  MainModule::class
])
class PresentationModule
