package me.redth.bordertext.mixin;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontRenderer.class)
public interface FontRendererAccessor {
    @Accessor("boldStyle")
    boolean isBoldStyle();
}
