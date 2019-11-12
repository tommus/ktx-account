package co.windly.ktxaccount.sample.kotlindagger.utility.account

import co.windly.ktxaccount.annotation.AccountScheme
import co.windly.ktxaccount.annotation.AccountScheme.Mode
import co.windly.ktxaccount.annotation.DefaultBoolean
import co.windly.ktxaccount.annotation.DefaultDouble
import co.windly.ktxaccount.annotation.DefaultFloat
import co.windly.ktxaccount.annotation.DefaultInt
import co.windly.ktxaccount.annotation.DefaultLong
import co.windly.ktxaccount.annotation.DefaultString

@AccountScheme(mode = Mode.SINGLE)
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
