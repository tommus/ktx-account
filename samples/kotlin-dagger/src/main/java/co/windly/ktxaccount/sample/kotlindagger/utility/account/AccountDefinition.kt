package co.windly.ktxaccount.sample.kotlindagger.utility.account

import co.windly.ktxaccount.annotation.DefaultLong
import co.windly.ktxaccount.annotation.DefaultString
import co.windly.ktxaccount.annotation.AccountScheme

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

  //region Token

  @DefaultString(value = "")
  internal val accessToken: String,

  @DefaultString(value = "")
  internal val refreshToken: String,

  @DefaultLong(value = 0L)
  internal val expirationDate: Long

  //endregion
)
