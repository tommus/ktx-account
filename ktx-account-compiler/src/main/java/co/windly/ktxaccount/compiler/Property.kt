package co.windly.ktxaccount.compiler

data class Property internal constructor(

  //region Field Name

  val fieldName: String,

  //endregion

  //region Property Name

  val propertyName: String?,

  //endregion

  //region Type

  val type: PropertyType,

  //endregion

  //region Default Value

  val defaultValue: String?,

  //endregion

  //region Comment

  val comment: String?,

  //endregion

  //region Enable Reactive

  val enableReactive: Boolean = true,

  //endregion

  //region Distinct Until Changed

  val distinctUntilChanged: Boolean = true

  //endregion

) {

  //region Field Name

  val fieldNameUpperCase: String
    get() = fieldName.replace("([A-Z]+)".toRegex(), "\\_$1").toUpperCase()

  //endregion
}
