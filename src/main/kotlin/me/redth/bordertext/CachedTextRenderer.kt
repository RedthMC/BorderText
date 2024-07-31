package me.redth.bordertext

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.redth.bordertext.config.ModConfig
import me.redth.bordertext.mixin.FontRendererAccessor
import me.redth.bordertext.mixin.OFFontRendererAccessor
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.util.*

object CachedTextRenderer : FontRenderer(mc.gameSettings, ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false) {
    private val asciiTexture = CachedTexture()
    private val allTexture = Array(256) { page -> CachedTexture(page) } + asciiTexture
    private var isLightColor = true

    override fun setColor(r: Float, g: Float, b: Float, a: Float) {
        val lightColor = (299f * r + 587f * g + 114f * b) / 1000f > ModConfig.threshold / 255f

        if (isLightColor != lightColor) {
            GL11.glTexEnvi(
                GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, when {
                    lightColor -> GL11.GL_MODULATE
                    else -> GL11.GL_ADD
                }
            )
            isLightColor = lightColor
        }

        super.setColor(r, g, b, a)
    }

    override fun drawString(text: String, x: Float, y: Float, color: Int, dropShadow: Boolean) =
        super.drawString(text, x, y, color, dropShadow).also {
            if (!isLightColor) {
                isLightColor = true
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE)
            }
        }

    override fun doDraw(f: Float) {
        GlStateManager.translate(0.0, 0.0, 0.1)
        super.doDraw(f)
        GlStateManager.translate(0.0, 0.0, -0.1)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        super.onResourceManagerReload(resourceManager)

        Arrays.stream(allTexture).parallel().forEach {
            it.load()
        }
        for (texture in allTexture) {
            texture.upload()
        }
    }

    override fun renderDefaultChar(ch: Int, italic: Boolean): Float {
        renderBorderedChar(
            texture = asciiTexture,
            italic = italic,
            ch = ch,
        )
        return getCharWidth(ch)
    }

    override fun renderUnicodeChar(char: Char, italic: Boolean): Float {
        val ch = char.code
        val widthBits = glyphWidth[ch].toInt()
        if (widthBits == 0) return 0f
        val first4bits = widthBits ushr 4

        renderBorderedChar(
            texture = allTexture[ch ushr 8],
            italic = italic,
            ch = ch,
            startPixels = first4bits,
        )

        val last4bits = (widthBits and 0xF) + 1
        return (last4bits - first4bits) / 2f + 1f
    }

    private fun renderBorderedChar(texture: CachedTexture, italic: Boolean, ch: Int, startPixels: Int = 0) {
        val italicShift = if (italic) 1.0f else 0.0f
        val column = ch and 0xF
        val row = (ch and 0xFF) ushr 4
        val x = posX - 1f - startPixels / 2f
        val y = posY - 1f
        val u = column / 16f
        val v = row / 16f
        val duv = 1f / 16f
        val dy = (20 - 0.02f) / 2f
        val dx = when {
            isBold() -> (21 - 0.02f) / 2f
            else -> (20 - 0.02f) / 2f
        }
        when {
            isBold() -> when {
                isLightColor -> texture.bold.bind()
                else -> texture.boldDark.bind()
            }

            else -> when {
                isLightColor -> texture.bordered.bind()
                else -> texture.borderedDark.bind()
            }
        }
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
        GL11.glTexCoord2f(u, v)
        GL11.glVertex3f(x + italicShift, y, 0.0f)
        GL11.glTexCoord2f(u, v + duv)
        GL11.glVertex3f(x - italicShift, y + dy, 0.0f)
        GL11.glTexCoord2f(u + duv, v)
        GL11.glVertex3f(x + dx + italicShift, y, 0.0f)
        GL11.glTexCoord2f(u + duv, v + duv)
        GL11.glVertex3f(x + dx - italicShift, y + dy, 0.0f)
        GL11.glEnd()
    }

    private fun isBold() = (this as FontRendererAccessor).isBoldStyle

    private fun getCharWidth(index: Int) = (this as OFFontRendererAccessor).charWidthFloat[index]
}