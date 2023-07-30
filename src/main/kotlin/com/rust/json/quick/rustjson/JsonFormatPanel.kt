package com.rust.json.quick.rustjson;

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.*
import com.intellij.ui.EditorTextField
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel


/**
 * format editor
 */
class JsonFormatPanel(private val event: AnActionEvent,
                      private val popupWidth: Int,
                      private val popupHeight: Int) : JPanel() {

    init {
        isEnabled = true
        size = Dimension(popupWidth, popupHeight)

        val editor = createJsonEditor()
        add(editor)
        add(createPreferencePanel(editor))
    }

    // create json editor
    private fun createJsonEditor(): EditorTextField {
        val editor = JsonEditor()
        editor.preferredSize = Dimension(popupWidth - 100, popupHeight)
        return editor
    }

    // create preference panel
    private fun createPreferencePanel(editor: EditorTextField): JPanel {
        val vBox = JPanel()
        vBox.layout = BoxLayout(vBox, BoxLayout.Y_AXIS)

        // 创建多个checkbox
        val serdeCheckbox = JCheckBox("serde json")
        val fieldCheckbox = JCheckBox("public field")

        // 创建按钮
        val formatButton = JButton("Format")
        val okButton = JButton("OK")

        formatButton.addActionListener {
            if (editor.text.isEmpty()) {
                return@addActionListener
            }

            val result = runCatching {
                val jsonString = editor.text
                val gson = GsonBuilder().setPrettyPrinting().setLenient().create()
                val jsonElement: JsonElement = JsonParser.parseString(jsonString)
                val formattedJsonString = gson.toJson(jsonElement)
                editor.text = formattedJsonString
            }
            println("result: $result")
        }


        // ok button
        okButton.addActionListener {
//            if (editor.text.isEmpty()) {
//                return@addActionListener
//            }

            // 获取当前编辑的文件
            val file: PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return@addActionListener
            // 在写入命令的上下文中执行代码插入操作
            WriteCommandAction.runWriteCommandAction(event.project!!, Runnable {

                val result = runCatching {
                    val jsonString = editor.text
                    val gson = GsonBuilder().setPrettyPrinting().setLenient().create()
                    val jsonElement: JsonElement = JsonParser.parseString(jsonString)

                    // 解析
                    parseJson2RustStruct(jsonElement)

                    val newFile = PsiFileFactory.getInstance(event.project)
                            .createFileFromText(PlainTextLanguage.INSTANCE, file.text + "\n" + "System.out.println(\"Hello, World!\")")
                    // 获取workspace当前文件的编辑器
                    val editor = event.getData<Editor>(CommonDataKeys.EDITOR)
                            ?: return@Runnable
                    // 刷新workspace编辑器
                    editor.document.setText(newFile.text)
                    editor.selectionModel.removeSelection()
                    editor.caretModel.moveToOffset(0)
                }

                System.err.println("result: $result")
            })
        }

        // 将checkbox添加到面板中
        vBox.add(serdeCheckbox)
        vBox.add(fieldCheckbox)
        vBox.add(formatButton)
        vBox.add(okButton)
        return vBox
    }

    private fun parseJson2RustStruct(jsonElement: JsonElement) {
//        if (jsonElement is JsonPrimitive) {
//            val jsonPrimitive = jsonElement.asJsonPrimitive
//            if (jsonPrimitive.isString) {
//                println("value: ${jsonPrimitive.asString}")
//            } else if (jsonPrimitive.isNumber) {
//                println("value: ${jsonPrimitive.asNumber}")
//            } else if (jsonPrimitive.isBoolean) {
//                println("value: ${jsonPrimitive.asBoolean}")
//            }
//            return
//        }

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
    }
}
