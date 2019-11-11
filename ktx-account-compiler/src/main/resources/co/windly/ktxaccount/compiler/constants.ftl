package ${package};

<#if comment??>
/**
 * ${comment?trim}
 */
</#if>
class ${constantsClassName} {

  companion object {
<#list descriptorList as descriptor>

    //region ${descriptor.fieldName?cap_first}

    <#if descriptor.comment??>
    /**
     * ${descriptor.comment?trim}
     */
    </#if><#t>
    val KEY_${descriptor.fieldNameUpperCase} = "${descriptor.propertyName}";

    /**
     * Default value stored for the property under "${descriptor.propertyName}" key.
     */
    val DEFAULT_${descriptor.fieldNameUpperCase} = ${descriptor.defaultValue};

    //endregion
</#list>
  }
}
