package com.bean.quarkshim;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod("quark_client_shim")
public class QuarkClientShim {

    public static final String QUARK_NS = "quark";
    private static final Gson GSON = new Gson();
    private static ShimConfig CFG = new ShimConfig();

    public QuarkClientShim() {
        loadOrCreateConfig();
    }

    private void loadOrCreateConfig() {
        try {
            Path p = FMLPaths.CONFIGDIR.get().resolve("quark_client_shim.json");
            if (Files.notExists(p)) {
                CFG = ShimConfig.example();
                Files.writeString(p, GSON.toJson(CFG));
            } else {
                CFG = GSON.fromJson(Files.readString(p), ShimConfig.class);
                if (CFG == null) CFG = ShimConfig.example();
            }
        } catch (IOException e) {
            CFG = ShimConfig.example();
        }
    }

    @Mod.EventBusSubscriber(modid = "quark_client_shim", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Registrar {

        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
                var toAdd = new ArrayList<ResourceLocation>();
                for (String path : CFG.blocks) {
                    ResourceLocation key = new ResourceLocation(QUARK_NS, path);
                    if (!ForgeRegistries.BLOCKS.containsKey(key)) toAdd.add(key);
                }
                event.register(ForgeRegistries.Keys.BLOCKS, helper -> {
                    for (ResourceLocation key : toAdd) {
                        helper.register(key, new Block(BlockBehaviour.Properties.of().strength(0.0F).noOcclusion()));
                    }
                });
            }

            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
                var toAdd = new ArrayList<ResourceLocation>();
                for (String path : CFG.items) {
                    ResourceLocation key = new ResourceLocation(QUARK_NS, path);
                    if (!ForgeRegistries.ITEMS.containsKey(key)) toAdd.add(key);
                }
                event.register(ForgeRegistries.Keys.ITEMS, helper -> {
                    for (ResourceLocation key : toAdd) {
                        helper.register(key, new Item(new Item.Properties()));
                    }
                });
            }


            if (event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS)) {
                var toAdd = new java.util.ArrayList<net.minecraft.resources.ResourceLocation>();
                for (String path : CFG.sounds) {
                    ResourceLocation key = new ResourceLocation(QUARK_NS, path);
                    if (!ForgeRegistries.SOUND_EVENTS.containsKey(key)) toAdd.add(key);
                }
                event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
                    for (ResourceLocation key : toAdd) {
                        // Variable-range dummy sound event (safe default in 1.20.1)
                        helper.register(key, SoundEvent.createVariableRangeEvent(key));
                    }
                });
            }

            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES)) {
                var toAdd = new ArrayList<ResourceLocation>();
                for (String path : CFG.entities) {
                    ResourceLocation key = new ResourceLocation(QUARK_NS, path);
                    if (!ForgeRegistries.ENTITY_TYPES.containsKey(key)) toAdd.add(key);
                }
                event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> {
                    for (ResourceLocation key : toAdd) {
                        EntityType<Entity> type = (EntityType<Entity>) (EntityType.Builder.<Entity>of((et, lvl) -> null, MobCategory.MISC).sized(0.1f,0.1f).build(key.toString()));
                        helper.register(key, type);
                    }
                });
            }
        }
    }

    public static class ShimConfig {
        public List<String> blocks = new ArrayList<>();
        public List<String> items = new ArrayList<>();
        public List<String> entities = new ArrayList<>();
        public List<String> sounds = new ArrayList<>();
        public static ShimConfig example() {
            ShimConfig c = new ShimConfig();
            c.blocks.add("ancient_button");
            c.items.add("ancient_button");
            c.entities.add("stool");
            c.sounds.add("block.pipe.leak"); // quark:block.pipe.leak (example)
            return c;
        }
    }
}