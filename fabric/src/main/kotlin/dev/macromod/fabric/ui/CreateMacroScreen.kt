//? if >=1.21 {
package dev.macromod.fabric.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.FileAlreadyExistsException

class CreateMacroScreen(
    private val parent: Screen,
    private val reloadMacros: () -> Unit
) : Screen(Component.literal("Criar novo macro")) {

    private lateinit var keyField: EditBox
    private var errorMessage = ""

    override fun init() {

        keyField = EditBox(
            font,
            width / 2 - 100,
            65,
            200,
            20,
            Component.literal("Tecla")
        )

        keyField.setMaxLength(3)
        addRenderableWidget(keyField)

        addRenderableWidget(
            Button.builder(Component.literal("Criar macro")) {
                createMacro()
            }.bounds(width / 2 - 100, 120, 200, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Cancelar")) {
                onClose()
            }.bounds(width / 2 - 100, 145, 200, 20)
                .build()
        )
    }

    private fun createMacro() {

        val key = keyField.value.trim().uppercase()

        if (!key.matches(Regex("[A-Z0-9]|F([1-9]|1[0-2])"))) {
            errorMessage = "Tecla invalida!"
            return
        }

        if (key == "F10" || key == "F12") {
            errorMessage = "Esta tecla esta reservada!"
            return
        }

        try {
            val folder = FabricLoader.getInstance()
                .configDir
                .resolve("macromod")
                .resolve("macros")

            Files.createDirectories(folder)

            val file = folder.resolve("$key.txt")

            val script = "\$\${ log(\"Novo macro!\"); }\$\$"

            Files.write(
                file,
                script.toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            )

            reloadMacros()
            onClose()

        } catch (e: FileAlreadyExistsException) {
            errorMessage = "Ja existe um macro nessa tecla!"
        } catch (e: Exception) {
            errorMessage = "Erro ao criar o macro!"
        }
    }

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.render(graphics, mouseX, mouseY, delta)

        graphics.drawCenteredString(
            font, title, width / 2, 20,
            0xFFFFFFFF.toInt()
        )

        graphics.drawCenteredString(
            font,
            Component.literal("Digite a tecla de ativacao"),
            width / 2, 45,
            0xFFAAAAAA.toInt()
        )

        graphics.drawCenteredString(
            font,
            Component.literal(errorMessage),
            width / 2, 95,
            0xFFFF5555.toInt()
        )
    }

    override fun onClose() {
        Minecraft.getInstance().setScreen(parent)
    }

    override fun isPauseScreen(): Boolean = false
}
//?}