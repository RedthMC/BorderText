package me.redth.bordertext.mixin;

import me.redth.bordertext.CachedTextRenderer;
import me.redth.bordertext.cache.TextWidthCache;
import me.redth.bordertext.config.ModConfig;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = FontRenderer.class, priority = 1100)
public abstract class FontRendererMixin {
    @Shadow
    protected abstract float renderChar(char ch, boolean italic);

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    private void overrideShadow(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof CachedTextRenderer) return;
        if (ModConfig.INSTANCE.enabled) {
            cir.setReturnValue(CachedTextRenderer.INSTANCE.drawString(text, x, y, color, false) + (dropShadow ? 1 : 0));
        }
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    private void useCacheWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getCache()) {
            Integer cachedWidth = TextWidthCache.INSTANCE.getCachedWidth(text);
            if (cachedWidth != null) cir.setReturnValue(cachedWidth);
        }
    }

    @Inject(method = "getStringWidth", at = @At("TAIL"))
    private void cacheWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.INSTANCE.enabled && ModConfig.INSTANCE.getCache()) {
            TextWidthCache.INSTANCE.putCache(text, cir.getReturnValueI());
        }
    }

    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderChar(CZ)F", ordinal = 1))
    private float overrideShadow(FontRenderer instance, char c, boolean ch) {
        if (ModConfig.INSTANCE.enabled) return 0f;
        return renderChar(c, ch);
    }
}
