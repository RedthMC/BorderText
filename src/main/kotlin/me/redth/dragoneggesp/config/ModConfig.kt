package me.redth.dragoneggesp.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.redth.dragoneggesp.BlurTest

object ModConfig : Config(Mod(BlurTest.NAME, ModType.UTIL_QOL), "${BlurTest.MODID}.json") {

    @HUD(name = "text")
    var text = TextOutline()

    @Slider(name = "threshold", min = 0f, max = 255f)
    var threshold = 128

    init {
        initialize()
    }


}