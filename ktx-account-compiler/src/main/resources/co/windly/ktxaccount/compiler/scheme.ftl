package ${package};

import android.content.Context
import co.windly.ktxaccount.runtime.scheme.BaseAccountScheme
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
abstract class ${schemeClassName}(context: Context) : BaseAccountScheme(context) {

  //region Clear

  open fun clear(name: String) {
    <#list descriptorList as descriptor>

    // Clear value under "${descriptor.propertyName}" property and emit default value to it's subject.
    remove${descriptor.fieldName?cap_first}(name)
      <#if descriptor.enableReactive>.also {
        retrieve${descriptor.fieldName?cap_first}Subject(name)
          .onNext(DEFAULT_${descriptor.fieldNameUpperCase})
      }
      </#if>
    </#list>
  }

  <#if classEnableReactive>
  open fun clearRx(name: String): Completable =
    Completable
      .fromAction { clear(name) }
      .subscribeOn(Schedulers.io())

  </#if>
  //endregion

  //region Account

  override fun saveAccount(name: String): Completable =
    super
      .saveAccount(name)
      .andThen(initializeSubjects(name))

  override fun saveAccount(name: String, password: String): Completable =
    super
      .saveAccount(name, password)
      .andThen(initializeSubjects(name))

  private fun initializeSubjects(name: String): Completable = Completable.fromAction {
    <#list descriptorList as descriptor>

    // Initialize "${descriptor.propertyName}" property subject if not yet initialized.
    if (${descriptor.fieldName}Subject == null) {
      ${descriptor.fieldName}Subject = BehaviorSubject.createDefault(get${descriptor.fieldName?cap_first}(name))
    }

    // Otherwise emit persisted "${descriptor.propertyName}" property to already existing subject.
    else {
      ${descriptor.fieldName}Subject?.onNext(get${descriptor.fieldName?cap_first}(name))
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

  protected fun retrieve${descriptor.fieldName?cap_first}Subject(name: String): BehaviorSubject<${descriptor.type.simpleName}> {

    // Retrieve nullable account.
    val account = retrieveNullableAccount(name)

    // Initialize subject if not initialized.
    if (${descriptor.fieldName}Subject == null) {
      if (account == null) {
        ${descriptor.fieldName}Subject = BehaviorSubject.create()
      } else {
        ${descriptor.fieldName}Subject = BehaviorSubject.createDefault(get${descriptor.fieldName?cap_first}(name))
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
  open fun save${descriptor.fieldName?cap_first}(name: String, ${descriptor.fieldName}: ${descriptor.type.simpleName}) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Save property.
    manager
      .setUserData(account, KEY_${descriptor.fieldNameUpperCase}, ${descriptor.fieldName}<#if descriptor.type != "STRING">.toString()</#if>)
    <#if descriptor.enableReactive>

    // Update subject.
    retrieve${descriptor.fieldName?cap_first}Subject(name).onNext(${descriptor.fieldName})
    </#if>
  }

  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun get${descriptor.fieldName?cap_first}(name: String): ${descriptor.type.simpleName} {

    // Retrieve account.
    val account = retrieveAccount(name)

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
  open fun remove${descriptor.fieldName?cap_first}(name: String) {

    // Retrieve account.
    val account = retrieveAccount(name)

    // Remove property.
    manager.setUserData(account, KEY_${descriptor.fieldNameUpperCase}, null)
    <#if descriptor.enableReactive>

    // Update subject.
    retrieve${descriptor.fieldName?cap_first}Subject(name).onNext(DEFAULT_${descriptor.fieldNameUpperCase})
    </#if>
  }

  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun saveRx${descriptor.fieldName?cap_first}(name: String, ${descriptor.fieldName}: ${descriptor.type.simpleName}): Completable =
    Completable
      .fromAction { save${descriptor.fieldName?cap_first}(name, ${descriptor.fieldName}) }
      .subscribeOn(Schedulers.io())

  </#if>
  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun getRx${descriptor.fieldName?cap_first}(name: String): Single<${descriptor.type.simpleName}> =
    Single
      .fromPublisher<${descriptor.type.simpleName}> {

        // Retrieve account.
        val account = retrieveNullableAccount(name)

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
  open fun removeRx${descriptor.fieldName?cap_first}(name: String): Completable =
    Completable
      .fromAction { remove${descriptor.fieldName?cap_first}(name) }

  </#if>
  <#if descriptor.enableReactive>
  <#if descriptor.comment??>
  /**
   * ${descriptor.comment?trim}
   */
  </#if><#t>
  open fun observeRx${descriptor.fieldName?cap_first}(name: String): Flowable<${descriptor.type.simpleName}> =
    retrieve${descriptor.fieldName?cap_first}Subject(name)
      .toFlowable(LATEST)
      <#if descriptor.distinctUntilChanged>
      .distinctUntilChanged()
      </#if>

  </#if>
  //endregion
</#list>
}
