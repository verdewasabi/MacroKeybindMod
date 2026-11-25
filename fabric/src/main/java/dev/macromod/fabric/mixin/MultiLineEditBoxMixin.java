//? if >=1.21.11 {
package dev.macromod.fabric.mixin;

import dev.macromod.fabric.ui.EditMacroScreen;
import dev.macromod.fabric.ui.MacroSyntaxHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.macromod.fabric.ui.MacroEditorAccess;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin
        implements MacroEditorAccess {

    @Shadow
    @Final
    private MultilineTextField textField;

    @Override
    public int macroCursor() {
        return this.textField.cursor();
    }

    @Override
    public boolean macroHasSelection() {
        return this.textField.hasSelection();
    }

    @Override
    public void macroInsertText(String text) {
        this.textField.insertText(text);
    }

    @Unique
    private String macromod$source;

    @Unique
    private int macromod$start;

    @Unique
    private int macromod$end;

    @Inject(method = "renderContents", at = @At("HEAD"))
    private void macromod$reset(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {
        macromod$source = null;
    }

    @Redirect(
            method = "renderContents",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;substring(II)Ljava/lang/String;",
                    remap = false
            )
    )
    private String macromod$captureText(
            String source,
            int start,
            int end
    ) {
        macromod$source = source;
        macromod$start = start;
        macromod$end = end;

        return source.substring(start, end);
    }

    @Redirect(
            method = "renderContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"
            )
    )
    private void macromod$drawHighlightedText(
            GuiGraphics graphics,
            Font font,
            String text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        String source = macromod$source;
        macromod$source = null;

        if (Minecraft.getInstance().screen
                instanceof EditMacroScreen
                && source != null) {

            graphics.drawString(
                    font,
                    MacroSyntaxHighlighter.highlightRange(
                            source,
                            macromod$start,
                            macromod$end
                    ),
                    x,
                    y,
                    color,
                    shadow
            );

        } else {
            graphics.drawString(
                    font, text, x, y, color, shadow
            );
        }
    }
}
//?}