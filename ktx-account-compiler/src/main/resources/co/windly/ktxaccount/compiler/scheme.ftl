package ${package};

import android.content.Context
import co.windly.ktxaccount.runtime.scheme.BaseAccountScheme
<#list descriptorList as descriptor>
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.DEFAULT_${descriptor.fieldNameUpperCase}
import co.windly.ktxaccount.sample.kotlindagger.utility.account.AccountDefinitionConstants.Companion.KEY_${descriptor.fieldNameUpperCase}
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
    remove${descriptor.fieldName?cap_first}(name)
      <#if descriptor.enableReactive>
      .also { ${descriptor.fieldName}Subject.onNext(DEFAULT_${descriptor.fieldNameUpperCase}) }
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
<#list descriptorList as descriptor>

  //region ${descriptor.fieldName?cap_first}
  <#if descriptor.enableReactive>

  protected val ${descriptor.fieldName}Subject: BehaviorSubject<${descriptor.type.simpleName}>
    by lazy { BehaviorSubject.create<${descriptor.type.simpleName}>() }
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
    ${descriptor.fieldName}Subject.onNext(${descriptor.fieldName})
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
    ${descriptor.fieldName}Subject.onNext(DEFAULT_${descriptor.fieldNameUpperCase})
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
    ${descriptor.fieldName}Subject
      .toFlowable(LATEST)
      <#if descriptor.distinctUntilChanged>
      .distinctUntilChanged()
      </#if>

  </#if>
  //endregion
</#list>
}
