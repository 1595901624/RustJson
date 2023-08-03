package com.rust.json.quick.rustjson

import java.math.BigDecimal
import javax.print.attribute.IntegerSyntax

data class RustStruct(
    /**
     * struct name
     */
    val name: String = "",
    /**
     * struct fields
     */
    val fields: MutableList<RustField> = mutableListOf(),
    /**
     * serde derive
     */
    val serde: Boolean = true,
    /**
     * public struct
     */
    val public: Boolean = true,
    /**
     * add option
     */
    val option: Boolean = true,
    /**
     * debug derive
     */
    val debug: Boolean = true,

    /**
     * clone derive
     */
    val clone: Boolean = false,
) {

    /**
     * convert to rust struct string
     */
    fun toRustStructString(): String {
        // fix struct field name
        fields.forEach {
            it.fixedName = processFieldName(it)
        }

        val stringBuilder = StringBuilder()
        // add derive
        if (serde && debug && clone) {
            stringBuilder.append("#[derive(Serialize, Deserialize, Debug, Clone)]\n")
        } else if (serde && debug) {
            stringBuilder.append("#[derive(Serialize, Deserialize, Debug)]\n")
        } else if (serde && clone) {
            stringBuilder.append("#[derive(Serialize, Deserialize, Clone)]\n")
        } else if (debug && clone) {
            stringBuilder.append("#[derive(Debug, Clone)]\n")
        } else if (serde) {
            stringBuilder.append("#[derive(Serialize, Deserialize)]\n")
        } else if (debug) {
            stringBuilder.append("#[derive(Debug)]\n")
        } else if (clone) {
            stringBuilder.append("#[derive(Clone)]\n")
        }
        // add public
        if (public) {
            stringBuilder.append("pub ")
        }
        // add struct name
        stringBuilder.append("struct $name {\n")
        // add fields
        fields.forEachIndexed { index, it ->
            stringBuilder.append("\t")
            // add serde
            if (serde) {
                stringBuilder.append("#[serde(rename = \"${it.name}\")]\n")
                stringBuilder.append("\t")
            }
            // add public
            if (it.public) {
                stringBuilder.append("pub ")
            }
            // add field name
            stringBuilder.append("${it.fixedName}: ")
            // add option
            if (option) {
                stringBuilder.append("Option<")
            }
            // add field type
            stringBuilder.append(it.type.type)
            // add object name
            if (it.type == RustType.Vec) {
                stringBuilder.append("<${it.objectName}>")
            } else if (it.type == RustType.Obj) {
                stringBuilder.append("${it.objectName}")
            }
            // add option
            if (option) {
                stringBuilder.append(">")
            }
            stringBuilder.append(",\n")
            // add a blank line
            if (index != fields.size - 1) {
                stringBuilder.append("\n")
            }
        }
        // add end
        stringBuilder.append("}\n")
        return stringBuilder.toString()
    }

    /**
     * process special field name
     */
    fun processFieldName(field: RustField): String {
        var tempName = field.fixedName.convertCamelToSnakeCase()

        // if name is rust keyword, add "struct name" as prefix
        if (tempName.isDefaultRustKeyword()) {
            tempName = "${name.convertCamelToSnakeCase()}_$tempName"
        }

        if (tempName.isDigit()) {
            tempName = "${name.convertCamelToSnakeCase()}_$tempName"
        }

        // if name is duplicate, add "struct name" as prefix until not duplicate
        while (fields.filter { it.fixedName == tempName && it.hashCode() != field.hashCode() }.isNotEmpty()) {
            tempName = "${name.convertCamelToSnakeCase()}_$tempName"
        }

        return tempName
    }
}

data class RustField(
    val name: String,
    val type: RustType,
    val public: Boolean,
    /**
     * if type is Vec/Obj, objectName is the object name of Vec/Obj
     *
     * example:
     * [objectName] is "Person" in "Vec<Person>"
     * [objectName] is "Person" in "Person"
     */
    val objectName: String? = null
) {
    /**
     * if name is duplicate or rust default keywords, use [fixedName] instead of [name]
     * when init this class, the [fixedName] is same as [name]
     */
    var fixedName: String = name

    /**
     * calculate hashCode
     */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + name.hashCode()
        result = prime * result + type.hashCode()
        result = prime * result + public.hashCode()
        result = prime * result + (objectName?.hashCode() ?: 0)
        return result
    }
}

enum class RustType(val type: String = "") {
    Str("String"),
    Integer32("i32"),
    Integer64("i64"),
    Integer128("i128"),
    Float32("f32"),
    Float64("f64"),
    UnsignedInteger32("u32"),
    UnsignedInteger64("u64"),
    UnsignedInteger128("u128"),
    IntegerSize("isize"),
    UnsignedIntegerSize("usize"),
    Bool("bool"),
    Vec("Vec"),
    Obj("")
}

val I32_MAX = BigDecimal("2147483647")
val I32_MIN = BigDecimal("-2147483648")
val I64_MAX = BigDecimal("9223372036854775807")
val I64_MIN = BigDecimal("-9223372036854775808")
val I128_MAX = BigDecimal("170141183460469231731687303715884105727")
val I128_MIN = BigDecimal("-170141183460469231731687303715884105728")