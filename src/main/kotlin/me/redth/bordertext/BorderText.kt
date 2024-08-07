package me.redth.bordertext

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.RenderEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.redth.bordertext.cache.TextCache
import me.redth.bordertext.cache.TextWidthCache
import me.redth.bordertext.config.ModConfig
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = BorderText.MODID, name = BorderText.NAME, version = BorderText.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object BorderText {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        (mc.resourceManager as IReloadableResourceManager).registerReloadListener(CachedTextRenderer)
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    fun onRenderTickEnd(event: RenderEvent) {
        if (event.stage != Stage.END) return
        if (!ModConfig.cache) return
        TextCache.cleanUp()
        TextWidthCache.cleanUp()
    }
}
