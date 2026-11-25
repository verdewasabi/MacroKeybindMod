//? if >=1.21 {
package dev.macromod.fabric.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ConfirmScreen
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files

class MacroManagerScreen(
    private val getMacroNames: () -> List<String>,
    private val reloadMacros: () -> Unit
) : Screen(
    Component.literal("MacroKeybindMod - Gerenciador")
) {

    private var macroNames = emptyList<String>()
    private var page = 0
    private var rowsPerPage = 1

    private fun totalPages(): Int {
        return ((macroNames.size + rowsPerPage - 1) /
                rowsPerPage).coerceAtLeast(1)
    }

    private var errorMessage = ""

    private fun confirmDelete(macroName: String) {
        Minecraft.getInstance().setScreen(
            ConfirmScreen(
                { confirmed ->
                    errorMessage = ""

                    if (confirmed) {
                        try {
                            val file = FabricLoader.getInstance()
                                .configDir
                                .resolve("macromod")
                                .resolve("macros")
                                .resolve("$macroName.txt")

                            Files.delete(file)

                            reloadMacros()

                        } catch (e: Exception) {
                            errorMessage = "Erro ao excluir $macroName.txt"
                        }
                    }

                    Minecraft.getInstance().setScreen(this)
                },
                Component.literal("Excluir macro"),
                Component.literal(
                    "Deseja realmente excluir $macroName.txt?"
                ),
                Component.literal("Excluir"),
                Component.literal("Cancelar")
            )
        )
    }

    override fun init() {
        macroNames = getMacroNames()

        rowsPerPage = ((height - 155) / 20).coerceAtLeast(1)

        page = page.coerceIn(0, totalPages() - 1)

        val visibleMacros = macroNames
            .drop(page * rowsPerPage)
            .take(rowsPerPage)

        var y = 60

        for (name in visibleMacros) {
            addRenderableWidget(
                Button.builder(Component.literal("Editar")) {
                    Minecraft.getInstance().setScreen(
                        EditMacroScreen(this, name, reloadMacros)
                    )
                }.bounds(width / 2 + 5, y - 5, 65, 20)
                    .build()
            )

            addRenderableWidget(
                Button.builder(Component.literal("Excluir")) {
                    confirmDelete(name)
                }.bounds(width / 2 + 75, y - 5, 65, 20)
                    .build()
            )

            y += 20
        }

        addRenderableWidget(
            Button.builder(Component.literal("+ Criar macro")) {
                net.minecraft.client.Minecraft.getInstance()
                    .setScreen(
                        CreateMacroScreen(this, reloadMacros)
                    )
            }.bounds(width / 2 - 100, height - 95, 200, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Anterior")) {
                if (page > 0) {
                    page--
                    rebuildWidgets()
                }
            }.bounds(width / 2 - 100, height - 65, 75, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Proximo")) {
                if (page < totalPages() - 1) {
                    page++
                    rebuildWidgets()
                }
            }.bounds(width / 2 + 25, height - 65, 75, 20)
                .build()
        )

        addRenderableWidget(
            Button.builder(Component.literal("Fechar")) {
                onClose()
            }.bounds(width / 2 - 100, height - 35, 200, 20)
                .build()
        )
    }

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        super.render(graphics, mouseX, mouseY, delta)

        graphics.drawCenteredString(
            font, title, width / 2, 16,
            0xFFFFFFFF.toInt()
        )

        graphics.drawCenteredString(
            font,
            Component.literal(
                errorMessage.ifEmpty {
                    "${macroNames.size} macros carregados"
                }
            ),
            width / 2, 38,
            if (errorMessage.isEmpty())
                0xFFAAAAAA.toInt()
            else
                0xFFFF5555.toInt()
        )

        val visibleMacros = macroNames
            .drop(page * rowsPerPage)
            .take(rowsPerPage)

        var y = 60

        for (name in visibleMacros) {
            graphics.drawCenteredString(
                font,
                Component.literal("[$name]  $name.txt"),
                width / 2 - 65, y,
                0xFF55FF55.toInt()
            )

            y += 20
        }

        if (macroNames.isEmpty()) {
            graphics.drawCenteredString(
                font,
                Component.literal("Nenhum macro encontrado"),
                width / 2, 65,
                0xFFAAAAAA.toInt()
            )
        }

        graphics.drawCenteredString(
            font,
            Component.literal("${page + 1}/${totalPages()}"),
            width / 2, height - 59,
            0xFFFFFFFF.toInt()
        )
    }

    override fun isPauseScreen(): Boolean = false
}
//?}