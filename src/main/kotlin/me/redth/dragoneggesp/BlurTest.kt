package me.redth.dragoneggesp

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.redth.dragoneggesp.config.ModConfig
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = BlurTest.MODID, name = BlurTest.NAME, version = BlurTest.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object BlurTest {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        (mc.resourceManager as IReloadableResourceManager).registerReloadListener(CachedTextRenderer)
    }
}
