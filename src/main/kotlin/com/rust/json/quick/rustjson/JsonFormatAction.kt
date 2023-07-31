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

        val dialog = JsonToRustDialog(e)
        dialog.pack()
        dialog.isVisible = true
    }
}
