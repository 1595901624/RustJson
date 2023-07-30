package com.rust.json.quick.rustjson

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.ui.EditorTextField

class JsonEditor : EditorTextField() {
    override fun createEditor(): EditorEx {
        val editorEx = super.createEditor()
        editorEx.setHorizontalScrollbarVisible(true)
        editorEx.setVerticalScrollbarVisible(true)
        editorEx.isOneLineMode = false
        return editorEx
    }
}