package me.redth.bordertext.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "net.minecraft.client.gui.FontRenderer")
public interface OFFontRendererAccessor {
    @Dynamic("optifine")
    @Accessor(value = "charWidthFloat", remap = false)
    float[] getCharWidthFloat();
}
