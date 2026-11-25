//? if >=1.21.11 {
package dev.macromod.fabric.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import net.minecraft.client.input.KeyEvent
import org.lwjgl.glfw.GLFW

class EditMacroScreen(
    private val parent: Screen,
    private val macroName: String,
    private val reloadMacros: () -> Unit
) : Screen(Component.literal("Editando $macroName.txt")) {

    private lateinit var scriptField: MultiLineEditBox

    private var errorMessage = ""
    private var originalScript: String? = null

    private val file = FabricLoader.getInstance()
        .configDir
        .resolve("macromod")
        .resolve("macros")
        .resolve("$macroName.txt")

    override fun init() {

        val content = if (::scriptField.isInitialized) {
            scriptField.getValue()
        } else {
            try {
                Files.readString(file).also {
                    originalScript = it
                }
            } catch (e: Exception) {
                errorMessage = "Erro ao ler o arquivo!"
                ""
            }
        }

        scriptField = MultiLineEditBox.builder()
            .setX(55)
            .setY(45)
            .setShowBackground(true)
            .setPlaceholder(
                Component.literal("Digite seu script...")
            )
            .build(
                font,
                width - 85,
                (height - 195).coerceAtLeast(20),
                Component.literal("Script")
            )

        scriptField.setValue(content)

        addRenderableWidget(scriptField)

        addRenderableWidget(
            Button.builder(Component.literal("Salvar script")) {
                saveScript()
            }.bounds(width / 2 + 5, height - 35, 100, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Cancelar")) {
                onClose()
            }.bounds(width / 2 - 105, height - 35, 100, 20)
                .build()
        )
    }

    private fun saveScript() {

        val script = scriptField.getValue()

        if (script.isBlank()) {
            errorMessage = "O script nao pode estar vazio!"
            return
        }

        try {
            if (originalScript == null ||
                Files.readString(file) != originalScript
            ) {
                errorMessage = "Arquivo alterado. Reabra o editor!"
                return
            }

            Files.write(
                file,
                script.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )

            reloadMacros()
            onClose()

        } catch (e: Exception) {
            errorMessage = "Erro ao salvar o script!"
        }
    }

    private data class Completion(
        val source: String,
        val cursor: Int,
        val prefix: String,
        val options: List<String>
    )

    private var lastCompletion: Completion? = null
    private var selectedSuggestion = 0
    private var suggestionsDismissed = false

    private fun findCompletion(): Completion? {
        if (!::scriptField.isInitialized) return null
        if (!scriptField.isFocused) return null

        val access = scriptField as? MacroEditorAccess
            ?: return null

        if (access.macroHasSelection()) return null

        val source = scriptField.getValue()
        val cursor = access.macroCursor()
            .coerceIn(0, source.length)

        val next = source.getOrNull(cursor)

        if (next != null &&
            (next.isLetterOrDigit() || next == '_')
        ) return null

        val before = source.substring(0, cursor)

        val prefix = before.takeLastWhile {
            it.isLetterOrDigit() || it == '_'
        }

        if (prefix.isEmpty()) return null

        if (!prefix.first().isLetter() &&
            prefix.first() != '_'
        ) return null

        val options = MacroSyntaxHighlighter
            .suggestions(prefix)
            .filterNot {
                it.equals(prefix, ignoreCase = true)
            }

        if (options.isEmpty()) return null

        return Completion(source, cursor, prefix, options)
    }

    private fun activeCompletion(): Completion? {
        val current = findCompletion()

        if (current != lastCompletion) {
            lastCompletion = current
            selectedSuggestion = 0
            suggestionsDismissed = false
        }

        return if (suggestionsDismissed) null else current
    }

    private fun renderSuggestions(graphics: GuiGraphics) {
        val completion = activeCompletion() ?: return

        val x = 55
        val y = scriptField.y + scriptField.height + 5
        val panelWidth = (width - 85).coerceAtMost(240)

        val panelHeight = 20 + completion.options.size * 11

        graphics.fill(
            x, y,
            x + panelWidth,
            y + panelHeight,
            0xFF252526.toInt()
        )

        for ((index, suggestion) in
        completion.options.withIndex()) {

            val rowY = y + 3 + index * 11

            if (index == selectedSuggestion) {
                graphics.fill(
                    x + 2, rowY,
                    x + panelWidth - 2,
                    rowY + 11,
                    0xFF094771.toInt()
                )
            }

            graphics.drawString(
                font,
                suggestion,
                x + 6,
                rowY + 1,
                0xFFDCDCAA.toInt()
            )
        }

        graphics.drawString(
            font,
            "Setas: selecionar | TAB: completar",
            x + 5,
            y + panelHeight - 11,
            0xFFAAAAAA.toInt()
        )
    }

    private fun renderLineNumbers(graphics: GuiGraphics) {
        val lines = scriptField.getValue().split('\n')

        val lineHeight = font.lineHeight
        val scroll = scriptField.scrollAmount().toInt()

        val top = scriptField.y
        val bottom = top + scriptField.height
        val textWidth = (scriptField.width - 8).coerceAtLeast(1)

        graphics.fill(
            30, top, 54, bottom,
            0xFF181818.toInt()
        )

        graphics.enableScissor(30, top, 54, bottom)

        var y = top + 4 - scroll

        for ((index, line) in lines.withIndex()) {
            val number = (index + 1).toString()

            if (y + lineHeight >= top && y < bottom) {
                graphics.drawString(
                    font,
                    number,
                    49 - font.width(number),
                    y,
                    0xFF888888.toInt()
                )
            }

            val wrappedLines = font.split(
                Component.literal(line),
                textWidth
            ).size.coerceAtLeast(1)

            y += wrappedLines * lineHeight
        }

        graphics.disableScissor()

        graphics.fill(
            53, top, 54, bottom,
            0xFF444444.toInt()
        )
    }

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.render(graphics, mouseX, mouseY, delta)
        renderLineNumbers(graphics)
        renderSuggestions(graphics)

        graphics.drawCenteredString(
            font, title, width / 2, 15,
            0xFFFFFFFF.toInt()
        )

        graphics.drawCenteredString(
            font,
            Component.literal("Tecla de ativacao: $macroName"),
            width / 2, 30,
            0xFFAAAAAA.toInt()
        )

        graphics.drawCenteredString(
            font,
            Component.literal(errorMessage),
            width / 2, height - 55,
            0xFFFF5555.toInt()
        )
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        val completion = activeCompletion()

        if (completion != null) {
            when (event.key()) {

                GLFW.GLFW_KEY_UP -> {
                    selectedSuggestion =
                        (selectedSuggestion - 1)
                            .coerceAtLeast(0)

                    return true
                }

                GLFW.GLFW_KEY_DOWN -> {
                    selectedSuggestion =
                        (selectedSuggestion + 1)
                            .coerceAtMost(
                                completion.options.lastIndex
                            )

                    return true
                }

                GLFW.GLFW_KEY_TAB -> {
                    val access = scriptField
                            as? MacroEditorAccess

                    if (access != null) {
                        val suggestion = completion
                            .options[selectedSuggestion]

                        val remaining = suggestion
                            .substring(completion.prefix.length)

                        access.macroInsertText(remaining)

                        selectedSuggestion = 0
                        lastCompletion = findCompletion()
                        suggestionsDismissed = true

                        return true
                    }
                }

                GLFW.GLFW_KEY_ESCAPE -> {
                    suggestionsDismissed = true
                    return true
                }
            }
        }

        return super.keyPressed(event)
    }

    override fun onClose() {
        Minecraft.getInstance().setScreen(parent)
    }

    override fun isPauseScreen(): Boolean = false
}
//?}