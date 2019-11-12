package ${package};

import android.content.Context
<#if schemaMode == "multiple">
import co.windly.ktxaccount.runtime.scheme.MultipleAccountScheme
</#if>
<#if schemaMode == "single">
import co.windly.ktxaccount.runtime.scheme.SingleAccountScheme
</#if>
<#list descriptorList as descriptor>
import ${package}.AccountDefinitionConstants.Companion.DEFAULT_${descriptor.fieldNameUpperCase}
import ${package}.AccountDefinitionConstants.Companion.KEY_${descriptor.fieldNameUpperCase}
</#list>
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

<#if comment??>
/**
 * ${comment?trim}
 */
</#if>
abstract class ${schemeClassName}(context: Context) :
<#if schemaMode == "multiple">  MultipleAccountScheme(context) {</#if>
<#if schemaMode == "single">  SingleAccountScheme(context) {</#if>

  //region Clear

  open fun clear(<#if schemaMode == "multiple">name: String</#if>) {
    <#list descriptorList as descriptor>

    // Clear value under "${descriptor.propertyName}" property and emit default value to it's subject.
    remove${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name</#if>)
      <#if descriptor.enableReactive>.also {
        retrieve${descriptor.fieldName?cap_first}Subject(<#if schemaMode == "multiple">name</#if>)
          .onNext(DEFAULT_${descriptor.fieldNameUpperCase})
      }
      </#if>
    </#list>
  }

  <#if classEnableReactive>
  open fun clearRx(<#if schemaMode == "multiple">name: String</#if>): Completable =
    Completable
      .fromAction { clear(<#if schemaMode == "multiple">name</#if>) }
      .subscribeOn(Schedulers.io())

  </#if>
  //endregion

  //region Account

  override fun saveAccount(name: String): Completable =
    super
      .saveAccount(name)
      .andThen(initializeSubjects(<#if schemaMode == "multiple">name</#if>))

  override fun saveAccount(name: String, password: String): Completable =
    super
      .saveAccount(name, password)
      .andThen(initializeSubjects(<#if schemaMode == "multiple">name</#if>))

  private fun initializeSubjects(<#if schemaMode == "multiple">name: String</#if>): Completable = Completable.fromAction {
    <#list descriptorList as descriptor>

    // Initialize "${descriptor.propertyName}" property subject if not yet initialized.
    if (${descriptor.fieldName}Subject == null) {
      ${descriptor.fieldName}Subject = BehaviorSubject.createDefault(get${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name</#if>))
    }

    // Otherwise emit persisted "${descriptor.propertyName}" property to already existing subject.
    else {
      ${descriptor.fieldName}Subject?.onNext(get${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name</#if>))
    }
    </#list>
  }

  override fun removeAccount(name: String): Completable =
    super
      .removeAccount(name)
      .andThen(clearSubjects())

  private fun clearSubjects(): Completable =
    Completable
      .fromAction {
        <#list descriptorList as descriptor>

        // Tear down "${descriptor.propertyName}" property subject.
        if (${descriptor.fieldName}Subject != null) {
          ${descriptor.fieldName}Subject = null
        }
        </#list>
      }

  //endregion
<#list descriptorList as descriptor>

  //region ${descriptor.fieldName?cap_first}
  <#if descriptor.enableReactive>

  protected var ${descriptor.fieldName}Subject: BehaviorSubject<${descriptor.type.simpleName}>? =
    null

  protected fun retrieve${descriptor.fieldName?cap_first}Subject(<#if schemaMode == "multiple">name: String</#if>): BehaviorSubject<${descriptor.type.simpleName}> {

    // Retrieve nullable account.
    val account = retrieveNullableAccount(<#if schemaMode == "multiple">name</#if>)

    // Initialize subject if not initialized.
    if (${descriptor.fieldName}Subject == null) {
      if (account == null) {
        ${descriptor.fieldName}Subject = BehaviorSubject.create()
      } else {
        ${descriptor.fieldName}Subject = BehaviorSubject.createDefault(get${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name</#if>))
      }
    }

    // Return initialized subject.
    return requireNotNull(${descriptor.fieldName}Subject)
  }
  </#if>

  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun save${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String, </#if>${descriptor.fieldName}: ${descriptor.type.simpleName}) {

    // Retrieve account.
    val account = retrieveAccount(<#if schemaMode == "multiple">name</#if>)

    // Save property.
    manager
      .setUserData(account, KEY_${descriptor.fieldNameUpperCase}, ${descriptor.fieldName}<#if descriptor.type != "STRING">.toString()</#if>)
    <#if descriptor.enableReactive>

    // Update subject.
    retrieve${descriptor.fieldName?cap_first}Subject(<#if schemaMode == "multiple">name</#if>).onNext(${descriptor.fieldName})
    </#if>
  }

  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun get${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String</#if>): ${descriptor.type.simpleName} {

    // Retrieve account.
    val account = retrieveAccount(<#if schemaMode == "multiple">name</#if>)

    // Retrieve property.
    val property = manager.getUserData(account, KEY_${descriptor.fieldNameUpperCase})

    // Return property or it's default value.
    return when (property.isNullOrBlank()) {
      true -> DEFAULT_${descriptor.fieldNameUpperCase}
      else -> property<#if descriptor.type != "STRING">.to${descriptor.type.simpleName}()</#if>
    }
  }

  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun remove${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String</#if>) {

    // Retrieve account.
    val account = retrieveAccount(<#if schemaMode == "multiple">name</#if>)

    // Remove property.
    manager.setUserData(account, KEY_${descriptor.fieldNameUpperCase}, null)
    <#if descriptor.enableReactive>

    // Update subject.
    retrieve${descriptor.fieldName?cap_first}Subject(<#if schemaMode == "multiple">name</#if>).onNext(DEFAULT_${descriptor.fieldNameUpperCase})
    </#if>
  }

  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun saveRx${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String, </#if>${descriptor.fieldName}: ${descriptor.type.simpleName}): Completable =
    Completable
      .fromAction { save${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name, </#if>${descriptor.fieldName}) }
      .subscribeOn(Schedulers.io())

  </#if>
  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun getRx${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String</#if>): Single<${descriptor.type.simpleName}> =
    Single
      .fromPublisher<${descriptor.type.simpleName}> {

        // Retrieve account.
        val account = retrieveNullableAccount(<#if schemaMode == "multiple">name</#if>)

        // Emit an error for non-existent account.
        if (account == null) {
          it.onError(NoSuchElementException("Account does not exist."))
          it.onComplete()
          return@fromPublisher
        }

        // Retrieve property.
        val property = manager.getUserData(account, KEY_${descriptor.fieldNameUpperCase})

        // Complete with default value if property does not exist.
        if (property.isNullOrBlank()) {
          it.onNext(DEFAULT_${descriptor.fieldNameUpperCase})
          it.onComplete()
          return@fromPublisher
        }

        // Emit property.
        it.onNext(property.to${descriptor.type.simpleName}())
        it.onComplete()
      }
      .subscribeOn(Schedulers.io())

  </#if>
  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun removeRx${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String</#if>): Completable =
    Completable
      .fromAction { remove${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name</#if>) }

  </#if>
  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun observeRx${descriptor.fieldName?cap_first}(<#if schemaMode == "multiple">name: String</#if>): Flowable<${descriptor.type.simpleName}> =
    retrieve${descriptor.fieldName?cap_first}Subject(<#if schemaMode == "multiple">name</#if>)
      .toFlowable(LATEST)
      <#if descriptor.distinctUntilChanged>
      .distinctUntilChanged()
      </#if>

  </#if>
  //endregion
</#list>
}
