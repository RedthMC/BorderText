package me.redth.bordertext

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation

class Texture(path: String) : AbstractTexture() {
    private val location = ResourceLocation(path)
    private var data: IntArray? = null
    private var width: Int? = null
    private var height: Int? = null

    init {
        mc.textureManager.loadTexture(location, this)
    }

    fun save(data: IntArray, width: Int, height: Int) {
        this.data = data
        this.width = width
        this.height = height
    }

    fun upload() {
        val data = data ?: return
        val width = width ?: return
        val height = height ?: return
        val id = getGlTextureId()
        TextureUtil.allocateTexture(id, width, height)
        TextureUtil.uploadTexture(id, data, width, height)
    }

    fun bind() {
        mc.textureManager.bindTexture(location)
    }

    override fun loadTexture(resourceManager: IResourceManager) {}
}