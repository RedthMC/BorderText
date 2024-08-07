package me.redth.bordertext.cache

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11


object TextCache {
    private const val X_PRE_SPACE = 4
    private const val MAX_X = X_PRE_SPACE + 2 // [font width] + 1 [bold] + 2 [italic] + 1 [shadow]
    private const val MAX_X_PX = MAX_X * 2 // MAX_X * 2 [unicode pixel]
    private const val Y_PRE_SPACE = 2
    private const val MAX_Y = Y_PRE_SPACE + 10 // 8 [font height] + 1 [underline] + 1 [shadow]
    private const val MAX_Y_PX = MAX_Y * 2 // MAX_Y * 2 [unicode pixel]

    private val CACHE = HashMap<FontConfig, Cache>()

    fun getCache(key: FontConfig): Cache? {
        return CACHE[key]
    }

    fun putCache(key: FontConfig, cache: Cache) {
        CACHE[key] = cache
    }

    fun clearAll() {
        CACHE.clear()
    }

    fun cleanUp() {
        CACHE.entries.removeIf { (_, value): Map.Entry<FontConfig, Cache> -> !value.usedThisFrame }
    }

    inline fun createCache(config: FontConfig, allocate: Int, draw: () -> Int) =
        Cache(allocate).apply {
            startRender()
            val i = draw()
            stopRender(i)
            putCache(config, this)
        }

    class Cache(width: Int) {
        private val framebuffer = Framebuffer(width * 2 + MAX_X_PX, MAX_Y_PX, false)
        private val maxWidth = width + MAX_X
        var usedThisFrame = false
        private var drawnWidth = 0

        fun startRender() {
            framebuffer.bindFramebuffer(true)
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
            GlStateManager.ortho(0.0, framebuffer.framebufferWidth.toDouble(), framebuffer.framebufferHeight.toDouble(), 0.0, 1000.0, 3000.0)
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.pushMatrix()
            GlStateManager.loadIdentity()
            GlStateManager.translate(X_PRE_SPACE.toFloat(), Y_PRE_SPACE.toFloat(), -2000.0f) // x=2 for italic
            GlStateManager.scale(2f, 2f, 0f) // unicode pixels
        }

        fun stopRender(drawnWidth: Int) {
            GlStateManager.matrixMode(GL11.GL_PROJECTION) // GL_PROJECTION
            GlStateManager.popMatrix()
            GlStateManager.matrixMode(GL11.GL_MODELVIEW) // GL_MODELVIEW
            GlStateManager.popMatrix()
            Minecraft.getMinecraft().framebuffer.bindFramebuffer(true)
            this.drawnWidth = drawnWidth
        }

        fun draw(x: Float, y: Float): Int {
            framebuffer.bindFramebufferTexture()
            GlStateManager.pushMatrix()
            GlStateManager.translate(x - X_PRE_SPACE.toFloat() / 2, y - Y_PRE_SPACE / 2, 0f) // -1f : italic pre-space
            GlStateManager.color(1f, 1f, 1f, 1f)
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
            GL11.glTexCoord2f(0f, 0f)
            GL11.glVertex3f(0f, MAX_Y.toFloat(), 0f)
            GL11.glTexCoord2f(1f, 0f)
            GL11.glVertex3f(maxWidth.toFloat(), MAX_Y.toFloat(), 0f)
            GL11.glTexCoord2f(0f, 1f)
            GL11.glVertex3f(0f, 0f, 0f)
            GL11.glTexCoord2f(1f, 1f)
            GL11.glVertex3f(maxWidth.toFloat(), 0f, 0f)
            GL11.glEnd()
            GlStateManager.popMatrix()
            framebuffer.unbindFramebufferTexture()
            usedThisFrame = true
            return x.toInt() + drawnWidth
        }
    }
}
