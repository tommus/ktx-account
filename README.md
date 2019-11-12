# Ktx Account

[![Maven Central][mavenbadge-svg]][mavencentral] [![Travis (.org) branch][travisci-svg]][travisci] [![API][apibadge-svg]][apioverview] [![GitHub][license-svg]][license]

This library makes it simpler to use Account Manager to persist user-related data providing
reactive interface.

## Usage

### Add dependencies

Add dependencies to the *Kotlin-based* project:

```groovy
dependencies {
    implementation "co.windly:ktx-account:1.1.1"
    kapt "co.windly:ktx-account-compiler:1.1.1"
}
```

## Define schema

Use the `@AccountSchema` annotation on any POJO. All (non static) fields will be considered an
Account Manager property.

Minimal example:

```kotlin
@AccountScheme
class AccountDefinition(

  //region Id

  @DefaultLong(value = 0L)
  internal val id: Long,

  //endregion

  //region Name

  @DefaultString(value = "")
  internal val firstName: String,

  @DefaultString(value = "")
  internal val lastName: String,

  //endregion

  //region Properties

  @DefaultInt(value = 0)
  internal val age: Int,

  @DefaultFloat(value = 0.0f)
  internal val height: Float,

  //endregion

  //region Location

  @DefaultDouble(value = 0.0)
  internal val latitude: Double,

  @DefaultDouble(value = 0.0)
  internal val longitude: Double,

  //endregion

  //region Token

  @DefaultString(value = "")
  internal val accessToken: String,

  @DefaultString(value = "")
  internal val refreshToken: String,

  @DefaultLong(value = 0L)
  internal val expirationDate: Long,

  //endregion

  //region Miscellaneous

  @DefaultBoolean(value = false)
  internal val active: Boolean

  //endregion
)
```

Accepted property field types are:

 - Boolean
 - Double
 - Float
 - Int
 - Long
 - String

## Use generated scheme class

A class named `<YourClassName>Scheme` will be generated in the same package (at compile time). Use it like this:

```kotlin
// Get access to scheme.
val scheme = AccountDefinitionScheme(/* Context */ this)

// Define account name.
val name = "john.snow@winterfell.pl"

// Create account.
scheme
  .saveAccount(name, "WinterIsComing")
  .doOnComplete(onComplete)
  .subscribe()
  .addTo(disposables)
    
// Put a single value.
scheme
  .saveRxId(name, 1L)
  .subscribe()
  .addTo(disposables)
  
// Put several values in chained stream.
Completable
  .mergeArrayDelayError(
    scheme.saveRxFirstName(name, "John"),
    scheme.saveRxLastName(name, "Snow"),
    scheme.saveRxAccessToken(name, "aaaaaaa (...)"),
    scheme.saveRxRefreshToken(name, "bbbbbbb (...)"),
    scheme.saveRxExpirationDate(name, 1573399868L)
  )
  .subscribe()
  .addTo(disposables)

// Access properties one by one.
scheme
  .getRxId(name)
  .subscribe { id -> Log.d(TAG, "id -> $id.") }
  .addTo(disposables)
  
// Clear all properties (mostly for testing purposes).
scheme
  .clearRx(name)
  .subscribe()
  .addTo(disposables)

// Remove account.
scheme
  .removeAccount(name)
  .subscribe { Log.d(TAG, "Account has been removed.") }
  .addTo(disposables)
```

## Reactive Extensions

Library supports generation of reactive methods (see sample above). You can disable this feature either by:

- annotating class with `@Reactive(value = false)`,
- annotating field with `@Reactive(value = false)`.

All property changes are emitted to given stream using `distinctUntilChanged()` method. You can configure this
behavior in `@Reactive` annotation (property `distinctUntilChanged`) for entire class or for each field separately.

## License

    Copyright 2019 Tomasz Dzieniak

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[apibadge-svg]: https://img.shields.io/badge/API-22%2B-brightgreen.svg?color=97ca00
[apioverview]: https://developer.android.com/about/versions/android-5.1
[license-svg]: https://img.shields.io/github/license/tommus/ktx-account.svg?color=97ca00
[license]: http://www.apache.org/licenses/LICENSE-2.0
[mavenbadge-svg]: https://img.shields.io/maven-central/v/co.windly/ktx-account.svg?color=97ca00
[mavencentral]: https://search.maven.org/artifact/co.windly/ktx-account
[travisci-svg]: https://img.shields.io/travis/tommus/ktx-account/master.svg?color=97ca00
[travisci]: https://travis-ci.org/tommus/ktx-account
