package co.windly.ktxaccount.sample.kotlinsimple.presentation.main

import android.app.Activity
import android.os.Bundle
import co.windly.ktxaccount.sample.kotlinsimple.R.layout
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {

  //region Companion

  companion object {
    const val TAG = "MainActivity"
  }

  //endregion

  //region Disposables

  private val disposables: CompositeDisposable
    by lazy { CompositeDisposable() }

  //endregion

  //region Lifecycle

  override fun onCreate(savedInstanceState: Bundle?) {

    // Inject dependencies.
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(layout.activity_main)

    // TODO:
  }

  //endregion
}
