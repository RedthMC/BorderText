package me.redth.dragoneggesp

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.util.ResourceLocation

class CachedTexture(path: String) {
    constructor() : this("textures/font/ascii.png")
    constructor(page: Int) : this("textures/font/unicode_page_%02x.png".format(page))

    private val original = ResourceLocation(path)
    val bordered = Texture("bordered:$path")
    val bold = Texture("bordered_bold:$path")
    val borderedDark = Texture("bordered_dark:$path")
    val boldDark = Texture("bordered_bold_dark:$path")

    fun load() {
        val texture = runCatching {
            TextureUtil.readBufferedImage(mc.resourceManager.getResource(original).inputStream)
        }.getOrNull() ?: return

        assert(texture.width != texture.height) { "bro why ur font not square" }
        var imageWidth = texture.width
        val glyphs = texture.getRGB(0, 0, imageWidth, imageWidth, null, 0, imageWidth)

        val scale = imageWidth < 256
        if (scale) {
            imageWidth *= 2
        }

        val glyphSize = imageWidth / 16
        val pixelSize = glyphSize / 16
        val borderedSize = pixelSize * 20
        val borderedMapSize = borderedSize * 16
        val boldSize = pixelSize * 21
        val boldMapSize = boldSize * 16
        val boldMap = IntArray(boldMapSize * borderedMapSize)
        val boldMapDark = IntArray(boldMapSize * borderedMapSize)
        val borderedMap = IntArray(borderedMapSize * borderedMapSize)
        val borderedMapDark = IntArray(borderedMapSize * borderedMapSize)


        for (row in 0..15) {
            val glyphY = row * glyphSize
            val borderY = row * borderedSize
            for (column in 0..15) {
                val glyphX = column * glyphSize
                val borderX = column * borderedSize
                val boldX = column * boldSize
                for (glyphYOffset in 0..<glyphSize) for (glyphXOffset in 0..<glyphSize) {
                    val glyphIndex = if (scale) {
                        ((glyphY + glyphYOffset) / 2) * (imageWidth / 2) + (glyphX + glyphXOffset) / 2
                    } else {
                        (glyphY + glyphYOffset) * imageWidth + glyphX + glyphXOffset
                    }
                    val color = glyphs[glyphIndex]
                    val alpha = color and 0xFF000000.toInt()
                    if (alpha == 0) continue

                    val shadowColor = color and 0xFCFCFC ushr 2 or alpha
                    val unshadowColor = color and 0xFEFEFE ushr 1 or alpha

                    for (yOffset in 0..4) {
                        val skipY = yOffset == 0 || yOffset == 4
                        for (xOffset in 0..4) {
                            if (((xOffset == 0 || xOffset == 4) && skipY) || (xOffset == 2 && yOffset == 2)) continue
                            val borderIndex = (borderY + glyphYOffset + yOffset * pixelSize) * borderedMapSize + borderX + glyphXOffset + xOffset * pixelSize
                            if (borderedMap[borderIndex] and 0xFF000000.toInt() != 0) continue
                            borderedMap[borderIndex] = shadowColor
                            borderedMapDark[borderIndex] = unshadowColor
                        }
                        for (xOffset in 0..5) {
                            if (((xOffset == 0 || xOffset == 5) && skipY) || ((xOffset == 2 || xOffset == 3) && yOffset == 2)) continue
                            val boldIndex = (borderY + glyphYOffset + yOffset * pixelSize) * boldMapSize + boldX + glyphXOffset + xOffset * pixelSize
                            if (boldMap[boldIndex] and 0xFF000000.toInt() != 0) continue
                            boldMap[boldIndex] = shadowColor
                            boldMapDark[boldIndex] = unshadowColor
                        }
                    }

                    val mainIndex = (borderY + glyphYOffset + 2 * pixelSize) * borderedMapSize + borderX + glyphXOffset + 2 * pixelSize
                    val bold1Index = (borderY + glyphYOffset + 2 * pixelSize) * boldMapSize + boldX + glyphXOffset + 2 * pixelSize
                    val bold2Index = (borderY + glyphYOffset + 2 * pixelSize) * boldMapSize + boldX + glyphXOffset + 2 * pixelSize + 1
                    borderedMap[mainIndex] = color
                    boldMap[bold1Index] = color
                    boldMap[bold2Index] = color
                    borderedMapDark[mainIndex] = 0xFF000000.toInt()
                    boldMapDark[bold1Index] = 0xFF000000.toInt()
                    boldMapDark[bold2Index] = 0xFF000000.toInt()
                }
            }
        }

        bordered.save(borderedMap, borderedMapSize, borderedMapSize)
        bold.save(boldMap, boldMapSize, borderedMapSize)
        borderedDark.save(borderedMapDark, borderedMapSize, borderedMapSize)
        boldDark.save(boldMapDark, boldMapSize, borderedMapSize)
    }

    fun upload() {
        bordered.upload()
        bold.upload()
        borderedDark.upload()
        boldDark.upload()
    }

}