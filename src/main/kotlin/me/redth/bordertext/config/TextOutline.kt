package me.redth.bordertext.config

import cc.polyfrost.oneconfig.hud.SingleTextHud
import me.redth.bordertext.CachedTextRenderer
import net.minecraft.client.renderer.GlStateManager

class TextOutline : SingleTextHud("cool", true) {

    override fun getText(example: Boolean) = "ok"

    override fun drawLine(line: String, x: Float, y: Float, scale: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0f)
        GlStateManager.scale(scale, scale, 1f)
        CachedTextRenderer.drawString(line, 0f, 0f, color.rgb, false)
        GlStateManager.translate(0f, 8f, 0f)
        GlStateManager.popMatrix()
    }
}