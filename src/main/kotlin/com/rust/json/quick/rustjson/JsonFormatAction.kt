package com.rust.json.quick.rustjson

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.IconButton
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.JBImageIcon
import java.awt.Dimension
import java.awt.Point
import java.awt.Toolkit


class JsonFormatAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        if (e.project == null || e.getData(CommonDataKeys.PSI_FILE) == null) {
            return
        }
//        Messages.showMessageDialog(e.project, "Hello from the context menu!", "Context Menu", Messages.getInformationIcon());

//        showPopEditor(e)
        val dialog = JsonToRustDialog(e)
        dialog.pack()
        dialog.isVisible = true
    }

    /**
     * show the pop editor
     */
    private fun showPopEditor(event: AnActionEvent) {
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val screenWidth = screenSize.getWidth().toInt()
        val screenHeight = screenSize.getHeight().toInt()

        val popupWidth = 600
        val popupHeight = 400

        val panel = JsonFormatPanel(event!!, popupWidth, popupHeight)

        val jbPopupFactory = JBPopupFactory.getInstance()
        val createComponentPopupBuilder = jbPopupFactory.createComponentPopupBuilder(
            panel,
            null
        )

        val point = Point(screenWidth / 2 - popupWidth / 2, screenHeight / 2 - popupHeight / 2)
        val relativePoint = RelativePoint(point)

        createComponentPopupBuilder
            .setMinSize(Dimension(popupWidth, popupHeight))
            .setResizable(true)
            .setMovable(true)
            .setTitle("Json To Rust Struct")
            .setFocusable(true)
            .setRequestFocus(true)
            .setModalContext(true)
            .setShowBorder(true)
            .setCancelButton(
                IconButton("Cancel", null, null)
            )
            .setCancelOnClickOutside(false)
            .createPopup()
            .show(relativePoint)
    }
}
