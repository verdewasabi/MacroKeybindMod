//? if >=1.21.11 {
package dev.macromod.fabric.ui

interface MacroEditorAccess {
    fun macroCursor(): Int

    fun macroHasSelection(): Boolean

    fun macroInsertText(text: String)
}
//?}