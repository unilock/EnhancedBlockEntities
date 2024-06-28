package foundationgames.enhancedblockentities;

import foundationgames.enhancedblockentities.client.model.ModelIdentifiers;
import foundationgames.enhancedblockentities.client.render.SignRenderManager;
import foundationgames.enhancedblockentities.client.resource.template.TemplateLoader;
import foundationgames.enhancedblockentities.config.EBEConfig;
import foundationgames.enhancedblockentities.util.DateUtil;
import foundationgames.enhancedblockentities.util.EBEUtil;
import foundationgames.enhancedblockentities.util.ResourceUtil;
import foundationgames.enhancedblockentities.util.WorldUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class EnhancedBlockEntities implements ClientModInitializer {
    public static final String ID = "enhancedblockentities";
    public static final String NAMESPACE = "ebe";
    public static final Logger LOG = LogManager.getLogger("Enhanced Block Entities");
    public static final EBEConfig CONFIG = new EBEConfig();

    public static final TemplateLoader TEMPLATE_LOADER = new TemplateLoader();

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(ID).ifPresent(mod -> {
            var roots = mod.getRootPaths();

            if (roots.size() > 0) {
                TEMPLATE_LOADER.setRoot(roots.get(0).resolve("templates"));
            }
        });

        WorldRenderEvents.END.register(SignRenderManager::endFrame);
        ClientTickEvents.END_WORLD_TICK.register(WorldUtil.EVENT_LISTENER);

        ModelIdentifiers.init();
        EBESetup.setupResourceProviders();
        ModelPredicateProviderRegistry.register(EBEUtil.id("is_christmas"), (stack, world, entity, seed) -> DateUtil.isChristmas() ? 1 : 0);

        load();
    }

    public static void reload(ReloadType type) {
        load();
        if (type == ReloadType.WORLD) {
            MinecraftClient.getInstance().worldRenderer.reload();
        } else if (type == ReloadType.RESOURCES) {
            MinecraftClient.getInstance().reloadResources();
        }
    }

    public static void load() {
        CONFIG.load();

        EnhancedBlockEntityRegistry.clear();
        ResourceUtil.resetBasePack();
        ResourceUtil.resetTopLevelPack();

        if (CONFIG.renderEnhancedChests) {
            EBESetup.setupChests();
            EBESetup.setupRRPChests();
        }

        if (CONFIG.renderEnhancedSigns) {
            EBESetup.setupSigns();
            EBESetup.setupRRPSigns();
        }

        if (CONFIG.renderEnhancedBells) {
            EBESetup.setupBells();
            EBESetup.setupRRPBells();
        }

        if (CONFIG.renderEnhancedBeds) {
            EBESetup.setupBeds();
            EBESetup.setupRRPBeds();
        }

        if (CONFIG.renderEnhancedShulkerBoxes) {
            EBESetup.setupShulkerBoxes();
            EBESetup.setupRRPShulkerBoxes();
        }

        if (CONFIG.renderEnhancedDecoratedPots) {
            EBESetup.setupDecoratedPots();
            EBESetup.setupRRPDecoratedPots();
        }
    }
}
