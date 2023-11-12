package com.rust.json.quick.rustjson

/**
 * parse config
 */
data class ParseConfig(
    val serdeDerive: Boolean = true,
    val debugDerive: Boolean = true,
    val cloneDerive: Boolean = true,
    val publicStruct: Boolean = true,
    val publicField: Boolean = true,
    val option: Boolean = true
)