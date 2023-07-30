package com.rust.json.quick.rustjson

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object JsonParseUtil {
    fun parseJsonObject(json: String): List<RustStruct> {
        val jsonElement = JsonParser.parseString(json)
//        return parse(jsonElement)
        return mutableListOf()
    }

    //        {
//            name: rust,
//            age: 18,
//            isMale: true,
//            address: {
//                country: China,
//                province: GuangDong,
//                city: ShenZhen
//            },
//            hobbies: []
//         }
    /**
     * parse json object
     */
    fun parseJsonObject(jsonElement: JsonElement, structName: String = "", structList: MutableList<RustStruct>) {
        // create rust struct
        val rustStruct = RustStruct(
            name = structName.ifEmpty { "Root" },
            fields = mutableListOf(),
            serde = true,
            public = true
        )
        // get json object
        val jsonObject = jsonElement as JsonObject
        // get json object entry set
        val entrySet = jsonObject.entrySet()
        // for each entry
        entrySet.forEach {
            if (it.value.isJsonPrimitive) {
                // if value is json primitive
                val jsonPrimitive = it.value.asJsonPrimitive
                if (jsonPrimitive.isString) {
                    val rustField = RustField(
                        name = it.key,
                        type = RustType.Str,
                        public = true
                    )
                    rustStruct.fields.add(rustField)
                    println("key: ${it.key}, value: ${jsonPrimitive.asString}")
                } else if (jsonPrimitive.isNumber) {
                    val rustField = RustField(
                        name = it.key,
                        type = RustType.Integer32,
                        public = true
                    )
                    rustStruct.fields.add(rustField)
                    println("key: ${it.key}, value: ${jsonPrimitive.asNumber}")
                } else if (jsonPrimitive.isBoolean) {
                    val rustField = RustField(
                        name = it.key,
                        type = RustType.Bool,
                        public = true
                    )
                    rustStruct.fields.add(rustField)
                    println("key: ${it.key}, value: ${jsonPrimitive.asBoolean}")
                }
            } else if (it.value.isJsonObject) {
                // if value is json object
                val rustField = RustField(
                    name = it.key,
                    type = RustType.Obj,
                    public = true,
                    objectName = it.key.toCapitalizeFirstLetter()
                )
                rustStruct.fields.add(rustField)
                parseJsonObject(it.value, it.key.toCapitalizeFirstLetter(), structList)
            } else if (it.value.isJsonArray) {
                // if value is json array
                val jsonArray = it.value.asJsonArray
//                for (jsonTempElement in jsonArray) {
//                    parse(jsonTempElement, it.key)
//                }
            }
        }
        structList.add(rustStruct)
    }

    /**
     * parse json array
     */
    private fun parseJsonArray(jsonElement: JsonElement, structName: String = "", structList: MutableList<RustStruct>) {

    }


    private fun parseJson2RustStruct(jsonElement: JsonElement): RustStruct {
        val jsonObject = jsonElement as JsonObject
        val entrySet = jsonObject.entrySet()
        for (entry in entrySet) {
            val key = entry.key
            val value = entry.value
            if (value.isJsonPrimitive) {
                val jsonPrimitive = value.asJsonPrimitive
                if (jsonPrimitive.isString) {
                    println("key: $key, value: ${jsonPrimitive.asString}")
                } else if (jsonPrimitive.isNumber) {
                    println("key: $key, value: ${jsonPrimitive.asNumber}")
                } else if (jsonPrimitive.isBoolean) {
                    println("key: $key, value: ${jsonPrimitive.asBoolean}")
                }
            } else if (value.isJsonObject) {
                parseJson2RustStruct(value)
            } else if (value.isJsonArray) {
                val jsonArray = value.asJsonArray
                for (jsonTempElement in jsonArray) {
                    parseJson2RustStruct(jsonTempElement)
                }
            }
        }

        return RustStruct("", mutableListOf(), false, false)
    }

}
