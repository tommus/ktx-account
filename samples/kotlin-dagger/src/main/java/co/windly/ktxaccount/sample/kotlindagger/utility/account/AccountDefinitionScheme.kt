package co.windly.ktxaccount.sample.kotlindagger.utility.account

import android.content.Context
import co.windly.ktxaccount.runtime.scheme.BaseAccountScheme
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_ACCESS_TOKEN
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_EXPIRATION_DATE
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_FIRST_NAME
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_ID
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_LAST_NAME
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_REFRESH_TOKEN
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_ACCESS_TOKEN
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_EXPIRATION_DATE
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_FIRST_NAME
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_ID
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_LAST_NAME
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_REFRESH_TOKEN
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

abstract class AccountDefinitionScheme(context: Context) : BaseAccountScheme(context) {

  //region Clear

  open fun clear(name: String) {
    removeId(name).also { idSubject.onNext(DEFAULT_ID) }
    removeFirstName(name).also { firstNameSubject.onNext(DEFAULT_FIRST_NAME) }
    removeLastName(name).also { lastNameSubject.onNext(DEFAULT_LAST_NAME) }
    removeAccessToken(name).also { accessTokenSubject.onNext(DEFAULT_ACCESS_TOKEN) }
    removeRefreshToken(name).also { refreshTokenSubject.onNext(DEFAULT_REFRESH_TOKEN) }
    removeExpirationDate(name).also { expirationDateSubject.onNext(DEFAULT_EXPIRATION_DATE) }
  }

  open fun clearRx(name: String): Completable =
    Completable
      .fromAction { clear(name) }
      .subscribeOn(Schedulers.io())

  //endregion

  //region Id

  protected val idSubject: BehaviorSubject<Long>
    by lazy { BehaviorSubject.create<Long>() }

  open fun saveId(name: String, id: Long) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_ID, id.toString())

    // Update subject.
    idSubject.onNext(id)
  }

  open fun getId(name: String): Long {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_ID)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_ID
      else -> property.toLong()
    }
  }

  open fun removeId(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_ID, null)

    // Update subject.
    idSubject.onNext(DEFAULT_ID)
  }

  open fun saveRxId(name: String, id: Long): Completable =
    Completable
      .fromAction { saveId(name, id) }
      .subscribeOn(Schedulers.io())

  open fun getRxId(name: String): Single<Long> =
    Single
      .fromPublisher<Long> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_ID)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_ID)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property.toLong())
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxId(name: String): Completable =
    Completable
      .fromAction { removeId(name) }

  open fun observeRxId(name: String): Flowable<Long> =
    idSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion

  //region FirstName

  protected val firstNameSubject: BehaviorSubject<String>
    by lazy { BehaviorSubject.create<String>() }

  open fun saveFirstName(name: String, firstName: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_FIRST_NAME, firstName)

    // Update subject.
    firstNameSubject.onNext(firstName)
  }

  open fun getFirstName(name: String): String {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_FIRST_NAME)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_FIRST_NAME
      else -> property
    }
  }

  open fun removeFirstName(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_FIRST_NAME, null)

    // Update subject.
    firstNameSubject.onNext(DEFAULT_FIRST_NAME)
  }

  open fun saveRxFirstName(name: String, firstName: String): Completable =
    Completable
      .fromAction { saveFirstName(name, firstName) }
      .subscribeOn(Schedulers.io())

  open fun getRxFirstName(name: String): Single<String> =
    Single
      .fromPublisher<String> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_FIRST_NAME)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_FIRST_NAME)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property)
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxFirstName(name: String): Completable =
    Completable
      .fromAction { removeFirstName(name) }

  open fun observeRxFirstName(name: String): Flowable<String> =
    firstNameSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion

  //region LastName

  protected val lastNameSubject: BehaviorSubject<String>
    by lazy { BehaviorSubject.create<String>() }

  open fun saveLastName(name: String, lastName: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_LAST_NAME, lastName)

    // Update subject.
    lastNameSubject.onNext(lastName)
  }

  open fun getLastName(name: String): String {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_LAST_NAME)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_LAST_NAME
      else -> property
    }
  }

  open fun removeLastName(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_LAST_NAME, null)

    // Update subject.
    lastNameSubject.onNext(DEFAULT_LAST_NAME)
  }

  open fun saveRxLastName(name: String, lastName: String): Completable =
    Completable
      .fromAction { saveLastName(name, lastName) }
      .subscribeOn(Schedulers.io())

  open fun getRxLastName(name: String): Single<String> =
    Single
      .fromPublisher<String> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_LAST_NAME)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_LAST_NAME)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property)
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxLastName(name: String): Completable =
    Completable
      .fromAction { removeLastName(name) }

  open fun observeRxLastName(name: String): Flowable<String> =
    lastNameSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion

  //region AccessToken

  protected val accessTokenSubject: BehaviorSubject<String>
    by lazy { BehaviorSubject.create<String>() }

  open fun saveAccessToken(name: String, accessToken: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_ACCESS_TOKEN, accessToken)

    // Update subject.
    accessTokenSubject.onNext(accessToken)
  }

  open fun getAccessToken(name: String): String {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_ACCESS_TOKEN)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_ACCESS_TOKEN
      else -> property
    }
  }

  open fun removeAccessToken(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_ACCESS_TOKEN, null)

    // Update subject.
    accessTokenSubject.onNext(DEFAULT_ACCESS_TOKEN)
  }

  open fun saveRxAccessToken(name: String, accessToken: String): Completable =
    Completable
      .fromAction { saveAccessToken(name, accessToken) }
      .subscribeOn(Schedulers.io())

  open fun getRxAccessToken(name: String): Single<String> =
    Single
      .fromPublisher<String> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_ACCESS_TOKEN)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_ACCESS_TOKEN)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property)
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxAccessToken(name: String): Completable =
    Completable
      .fromAction { removeAccessToken(name) }

  open fun observeRxAccessToken(name: String): Flowable<String> =
    accessTokenSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion

  //region RefreshToken

  protected val refreshTokenSubject: BehaviorSubject<String>
    by lazy { BehaviorSubject.create<String>() }

  open fun saveRefreshToken(name: String, refreshToken: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_REFRESH_TOKEN, refreshToken)

    // Update subject.
    refreshTokenSubject.onNext(refreshToken)
  }

  open fun getRefreshToken(name: String): String {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_REFRESH_TOKEN)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_REFRESH_TOKEN
      else -> property
    }
  }

  open fun removeRefreshToken(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_REFRESH_TOKEN, null)

    // Update subject.
    refreshTokenSubject.onNext(DEFAULT_REFRESH_TOKEN)
  }

  open fun saveRxRefreshToken(name: String, refreshToken: String): Completable =
    Completable
      .fromAction { saveRefreshToken(name, refreshToken) }
      .subscribeOn(Schedulers.io())

  open fun getRxRefreshToken(name: String): Single<String> =
    Single
      .fromPublisher<String> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_REFRESH_TOKEN)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_REFRESH_TOKEN)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property)
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxRefreshToken(name: String): Completable =
    Completable
      .fromAction { removeRefreshToken(name) }

  open fun observeRxRefreshToken(name: String): Flowable<String> =
    refreshTokenSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion

  //region ExpirationDate

  protected val expirationDateSubject: BehaviorSubject<Long>
    by lazy { BehaviorSubject.create<Long>() }

  open fun saveExpirationDate(name: String, expirationDate: Long) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager.setUserData(account, KEY_EXPIRATION_DATE, expirationDate.toString())

    // Update subject.
    expirationDateSubject.onNext(expirationDate)
  }

  open fun getExpirationDate(name: String): Long {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_EXPIRATION_DATE)

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_EXPIRATION_DATE
      else -> property.toLong()
    }
  }

  open fun removeExpirationDate(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_EXPIRATION_DATE, null)

    // Update subject.
    expirationDateSubject.onNext(DEFAULT_EXPIRATION_DATE)
  }

  open fun saveRxExpirationDate(name: String, expirationDate: Long): Completable =
    Completable
      .fromAction { saveExpirationDate(name, expirationDate) }
      .subscribeOn(Schedulers.io())

  open fun getRxExpirationDate(name: String): Single<Long> =
    Single
      .fromPublisher<Long> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_EXPIRATION_DATE)

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_EXPIRATION_DATE)
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property.toLong())
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  open fun removeRxExpirationDate(name: String): Completable =
    Completable
      .fromAction { removeExpirationDate(name) }

  open fun observeRxExpirationDate(name: String): Flowable<Long> =
    expirationDateSubject
      .toFlowable(LATEST)
      .distinctUntilChanged()

  //endregion
}
