package com.rust.json.quick.rustjson

data class RustStruct(
    val name: String = "",
    val fields: MutableList<RustField> = mutableListOf(),
    val serde: Boolean = true,
    val public: Boolean = true,
    val option: Boolean = true
) {

    /**
     * convert to rust struct string
     */
    fun toRustStructString(): String {
        val stringBuilder = StringBuilder()
        // add serde
        if (serde) {
            stringBuilder.append("#[derive(Serialize, Deserialize)]\n")
        }
        // add public
        if (public) {
            stringBuilder.append("pub ")
        }
        // add struct name
        stringBuilder.append("struct $name {\n")
        // add fields
        fields.forEach {
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
            stringBuilder.append("${it.name.convertCamelToSnakeCase()}: ")
            // add option
            if (option) {
                stringBuilder.append("Option<")
            }
            // add field type
            stringBuilder.append(it.type.type)
            // add object name
            if (it.objectName != null) {
                stringBuilder.append("${it.objectName}")
            }
            // add option
            if (option) {
                stringBuilder.append(">")
            }
            stringBuilder.append(",\n")
        }
        // add end
        stringBuilder.append("}\n")
        return stringBuilder.toString()
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
)

enum class RustType(val type: String = "") {
    Str("String"),
    Integer32("i32"),
    Integer64("i64"),
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