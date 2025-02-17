package me.redth.bordertext.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.redth.bordertext.BorderText

object ModConfig : Config(Mod(BorderText.NAME, ModType.UTIL_QOL), "${BorderText.MODID}.json") {

    @Slider(name = "threshold", min = 0f, max = 255f)
    var threshold = 128

    @Switch(name = "font cache")
    var cache = false

    init {
        initialize()
    }


}