package com.rust.json.quick.rustjson

import com.google.gson.*

object JsonParseUtil {

    /**
     * format json
     */
    fun format(json: String): String {
        runCatching {
            val gson = GsonBuilder().setPrettyPrinting().setLenient().create()
            val jsonElement: JsonElement = JsonParser.parseString(json)
            return gson.toJson(jsonElement)
        }
        return json
    }

    /**
     * parse json
     */
    fun parse(json: String): List<RustStruct> {
        val list = mutableListOf<RustStruct>()
        val jsonElement = JsonParser.parseString(json)

        val rustStruct = RustStruct(
            name = "Root",
            fields = mutableListOf(),
            serde = true,
            public = true
        )

        if (jsonElement.isJsonObject) {
            parseJsonObject(jsonElement, "Root", list)
        } else if (jsonElement.isJsonArray) {
            parseJsonArray("Root", jsonElement.asJsonArray, rustStruct, list)
        }
        return list
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
    private fun parseJsonObject(
        jsonElement: JsonElement,
        structName: String = "",
        structList: MutableList<RustStruct>
    ) {
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
                parseJsonPrimitive(it.key, jsonPrimitive, rustStruct)
//                if (jsonPrimitive.isString) {
//                    val rustField = RustField(
//                        name = it.key,
//                        type = RustType.Str,
//                        public = true
//                    )
//                    rustStruct.fields.add(rustField)
//                } else if (jsonPrimitive.isNumber) {
//                    val rustField = RustField(
//                        name = it.key,
//                        type = RustType.Integer32,
//                        public = true
//                    )
//                    rustStruct.fields.add(rustField)
//                } else if (jsonPrimitive.isBoolean) {
//                    val rustField = RustField(
//                        name = it.key,
//                        type = RustType.Bool,
//                        public = true
//                    )
//                    rustStruct.fields.add(rustField)
//                }
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
                parseJsonArray(it.key, it.value as JsonArray, rustStruct, structList)
            }
        }
        structList.add(rustStruct)
    }


    /**
     * build a new json object by json array
     */
    private fun buildNewJsonObjectByJsonArray(jsonArray: JsonArray): JsonElement {
        val map = mutableMapOf<String, Any>()
        for (jsonElement in jsonArray) {
            jsonElement.asJsonObject.entrySet().forEach {
                // if map not contains key, then set key and value
                if (!map.containsKey(it.key)) {
                    map[it.key] = it.value
                } else {
                    // if map contains key, and value is null, then set value
                    if (map[it.key] == null && it.value != null) {
                        map[it.key] = it.value
                    }
                }
            }
        }
        return JsonParser.parseString(Gson().toJson(map))
    }

    /**
     * parse json array
     *
     * [{
     *    name: rust,
     *    age: 18,
     *    isMale: true,
     *    address: {
     *    country: China,
     *    province: GuangDong,
     *    city: ShenZhen
     *    },
     *    hobbies: []
     *    },
     *    {
     *    name: rust2,
     *    age: 18,
     *    isMale: true,
     *    address: {
     *    country: China,
     *    province: GuangDong,
     *    city: ShenZhen
     *    },
     *    hobbies: []
     *    }
     * ]
     * [ "A", "B", "C"]
     */
    private fun parseJsonArray(
        keyName: String,
        valueArray: JsonArray,
        rustStruct: RustStruct,
        structList: MutableList<RustStruct>
    ) {
        // if value is json array
        if (valueArray.size() == 0) {
            // if array is empty
            val rustField = RustField(
                name = keyName,
                type = RustType.Vec,
                public = true,
                objectName = "String"
            )
            rustStruct.fields.add(rustField)
        } else {
            val jsonTempElement = valueArray[0]
            // Judge whether the first element of the array is a json object
            if (jsonTempElement.isJsonPrimitive) {
                // if value is json primitive
                val jsonPrimitive = jsonTempElement.asJsonPrimitive
                parseJsonPrimitive(keyName, jsonPrimitive, rustStruct)
//                if (jsonPrimitive.isString) {
//                    val rustField = RustField(
//                        name = keyName,
//                        type = RustType.Vec,
//                        public = true,
//                        objectName = "String"
//                    )
//                    rustStruct.fields.add(rustField)
//                } else if (jsonPrimitive.isNumber) {
//                    val rustField = RustField(
//                        name = keyName,
//                        type = RustType.Vec,
//                        public = true,
//                        objectName = "i32"
//                    )
//                    rustStruct.fields.add(rustField)
//                } else if (jsonPrimitive.isBoolean) {
//                    val rustField = RustField(
//                        name = keyName,
//                        type = RustType.Vec,
//                        public = true,
//                        objectName = "bool"
//                    )
//                    rustStruct.fields.add(rustField)
//                }
            } else if (jsonTempElement.isJsonObject) {
                // if value is json object
                val rustField = RustField(
                    name = keyName,
                    type = RustType.Vec,
                    public = true,
                    objectName = keyName.toCapitalizeFirstLetter()
                )
                rustStruct.fields.add(rustField)
                // build a new json object
                parseJsonObject(
                    buildNewJsonObjectByJsonArray(valueArray),
                    keyName.toCapitalizeFirstLetter(),
                    structList
                )
            }
        }
    }

    /**
     * parse json primitive
     */
    private fun parseJsonPrimitive(
        keyName: String,
        valuePrimitive: JsonPrimitive,
        rustStruct: RustStruct,
    ) {
        if (valuePrimitive.isString) {
            val rustField = RustField(
                name = keyName,
                type = RustType.Str,
                public = true
            )
            rustStruct.fields.add(rustField)
        } else if (valuePrimitive.isNumber) {
            val rustField = RustField(
                name = keyName,
                type = RustType.Integer32,
                public = true
            )
            rustStruct.fields.add(rustField)
        } else if (valuePrimitive.isBoolean) {
            val rustField = RustField(
                name = keyName,
                type = RustType.Bool,
                public = true
            )
            rustStruct.fields.add(rustField)
        }
    }
}

