//? if >=1.21.11 {
package dev.macromod.fabric.ui

import net.minecraft.network.chat.Component

object MacroSyntaxHighlighter {

    private val keywords = setOf(
        "if", "else", "elseif", "endif",
        "do", "while", "loop", "until",
        "foreach", "next", "break"
    )

    private val commands = setOf(
        "log", "echo", "wait",
        "key", "keydown", "keyup",
        "set", "unset", "toggle",
        "inc", "dec", "stop",
        "look", "lookrel"
    )

    private val tokenPattern = Regex(
        """"(?:\\.|[^"\\])*"|[#&@][a-zA-Z_][a-zA-Z0-9_]*|\b\d+(?:ms|s)?\b|\b[a-zA-Z_][a-zA-Z0-9_]*\b|\x24\x24\{|\}\x24\x24"""
    )

    fun suggestions(prefix: String): List<String> {
        if (prefix.isBlank()) return emptyList()

        return (commands + keywords)
            .filter {
                it.startsWith(prefix, ignoreCase = true)
            }
            .sorted()
            .take(5)
    }

    @JvmStatic
    fun highlightRange(
        source: String,
        start: Int,
        end: Int
    ): Component {
        val result = Component.empty()
        var offset = 0

        for (part in highlight(source).siblings) {
            val text = part.string

            val from = maxOf(start, offset)
            val to = minOf(end, offset + text.length)

            if (from < to) {
                result.append(
                    Component.literal(
                        text.substring(
                            from - offset,
                            to - offset
                        )
                    ).setStyle(part.style)
                )
            }

            offset += text.length

            if (offset >= end) break
        }

        return result
    }

    fun highlight(source: String): Component {

        val result = Component.empty()
        var position = 0

        for (match in tokenPattern.findAll(source)) {

            if (match.range.first > position) {
                result.append(
                    Component.literal(
                        source.substring(
                            position,
                            match.range.first
                        )
                    )
                )
            }

            val token = match.value

            val color = when {
                token.startsWith("\"") -> 0xCE9178

                token.first().isDigit() -> 0xB5CEA8

                token.startsWith("#") ||
                        token.startsWith("&") ||
                        token.startsWith("@") -> 0x9CDCFE

                token.lowercase() in keywords -> 0xC586C0

                token.lowercase() in commands -> 0xDCDCAA

                token == "\$\${" ||
                        token == "}\$\$" -> 0xFFD700

                else -> 0xD4D4D4
            }

            result.append(
                Component.literal(token)
                    .withColor(color)
            )

            position = match.range.last + 1
        }

        if (position < source.length) {
            result.append(
                Component.literal(
                    source.substring(position)
                )
            )
        }

        return result
    }
}
//?}