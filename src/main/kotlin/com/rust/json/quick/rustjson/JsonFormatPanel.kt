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
class JsonFormatPanel(
    private val event: AnActionEvent,
    private val popupWidth: Int,
    private val popupHeight: Int
) : JPanel() {

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

        // create format button
        val serdeCheckbox = JCheckBox("serde json")
        val fieldCheckbox = JCheckBox("public field")

        // create format button
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

            // get current file
            val file: PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return@addActionListener
            // insert code in write command context
            // runWriteCommandAction: https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html
            WriteCommandAction.runWriteCommandAction(event.project!!, Runnable {

                val result = runCatching {
                    val jsonString = editor.text
//                    val gson = GsonBuilder().setPrettyPrinting().setLenient().create()
//                    val jsonElement: JsonElement = JsonParser.parseString(jsonString)

                    // parse json
                    val list = JsonParseUtil.parse(jsonString)

                    val newFile = PsiFileFactory.getInstance(event.project)
                        .createFileFromText(
                            PlainTextLanguage.INSTANCE,
                            file.text + "\n" + "System.out.println(\"Hello, World!\")"
                        )
                    // get workspace current file editor
                    val editor = event.getData<Editor>(CommonDataKeys.EDITOR)
                        ?: return@Runnable
                    // refresh workspace editor
                    editor.document.setText(newFile.text)
                    editor.selectionModel.removeSelection()
                    editor.caretModel.moveToOffset(0)
                }

                System.err.println("result: $result")
            })
        }

        // add checkbox to panel
        vBox.add(serdeCheckbox)
        vBox.add(fieldCheckbox)
        vBox.add(formatButton)
        vBox.add(okButton)
        return vBox
    }

}
