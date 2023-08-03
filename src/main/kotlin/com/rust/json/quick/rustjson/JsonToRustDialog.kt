package com.rust.json.quick.rustjson

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*;
import java.awt.event.*;

/**
 * Json To Rust Struct Dialog
 */
class JsonToRustDialog(
    private val event: AnActionEvent,
) : JDialog() {
    private var contentPane: JPanel? = null
    private var buttonOK: JButton? = null
    private var buttonCancel: JButton? = null
    private var buttonFormat: JButton? = null
    private var jsonEditorTextArea: JTextArea? = null
    private var checkBoxDebug: JCheckBox? = null
    private var checkBoxSerde: JCheckBox? = null
    private var checkBoxPublic: JCheckBox? = null
    private var checkBoxOption: JCheckBox? = null
    private var checkBoxClone: JCheckBox? = null

    init {
        // set dialog title
        title = "Json To Rust Struct"

        // set dialog size
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val screenWidth = screenSize.getWidth().toInt()
        val screenHeight = screenSize.getHeight().toInt()

        val popupWidth = 600
        val popupHeight = 500

        minimumSize = Dimension(popupWidth, popupHeight)
        setLocation(screenWidth / 2 - popupWidth / 2, screenHeight / 2 - popupHeight / 2)
        setContentPane(contentPane)
        isModal = true

        // set icon
//        val icon = ImageIcon(javaClass.getResource("/icon/rust.png"))
//        icon?.let {
//            setIconImage(it.image)
//        }

        getRootPane().defaultButton = buttonOK

        buttonOK?.addActionListener { onOK() }
        buttonCancel?.addActionListener { onCancel() }
        buttonFormat?.addActionListener { onFormat() }

        // call onCancel() when cross is clicked
        defaultCloseOperation = DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        contentPane?.registerKeyboardAction({
            onCancel()
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private fun onOK() {
        // get current file
        val file: PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        // insert code in write command context
        // runWriteCommandAction: https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html
        WriteCommandAction.runWriteCommandAction(event.project!!, Runnable {

            runCatching {
                val jsonString = jsonEditorTextArea!!.text

                // parse json
                val parseConfig = ParseConfig(
                    serdeDerive = checkBoxSerde!!.isSelected,
                    debugDerive = checkBoxDebug!!.isSelected,
                    cloneDerive = checkBoxClone!!.isSelected,
                    publicStruct = checkBoxPublic!!.isSelected,
                    option = checkBoxOption!!.isSelected
                )
                val list = JsonParseUtil(parseConfig).parse(jsonString)
                // generate rust code
                val codeStringBuilder = StringBuilder()
                for (rustStruct in list) {
                    codeStringBuilder.append(rustStruct.toRustStructString())
                }

                // insert code to file
                val newFile = PsiFileFactory.getInstance(event.project)
                    .createFileFromText(
                        PlainTextLanguage.INSTANCE,
                        file.text + "\n" + codeStringBuilder.toString()
                    )
                // get workspace current file editor
                val currentEditor = event.getData<Editor>(CommonDataKeys.EDITOR)
                    ?: return@Runnable
                // refresh workspace editor
                currentEditor.document.setText(newFile.text)
                currentEditor.selectionModel.removeSelection()
                currentEditor.caretModel.moveToOffset(0)
            }

            dispose()
        })
    }

    private fun onCancel() {
        dispose()
    }

    private fun onFormat() {
        val json = jsonEditorTextArea?.text
        if (!json.isNullOrEmpty()) {
            val formatJson = json.formatJson()
            jsonEditorTextArea?.text = formatJson
        }
    }

}
