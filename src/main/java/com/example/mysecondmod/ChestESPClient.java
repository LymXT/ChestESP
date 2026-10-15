package com.example.mysecondmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

/**
 * Client-side entry point for the ChestESP mod (v3.1) for Fabric 26.3.
 * Extended ChestESP featuring through-wall highlight outlines for chests, trapped chests,
 * ender chests, all shulker box colors, and chest minecarts.
 */
public class ChestESPClient implements ClientModInitializer {

    public static final String MOD_ID = "chestesp";

    /** Key binding for toggling ESP (bound to 'V' by default). */
    private static KeyMapping toggleKey;

    /** Current active state of the ESP. */
    private static boolean enabled = true;

    /** Maximum search radius (64 blocks). */
    public static final double MAX_RADIUS = 64.0;
    public static final double MAX_RADIUS_SQ = MAX_RADIUS * MAX_RADIUS;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    @Override
    public void onInitializeClient() {
        // 1. Register key binding in the MISC category
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.chestesp.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_V,
                KeyMapping.Category.MISC
        ));

        // 2. Handle key presses during the client tick cycle
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;
                if (client.player != null) {
                    Component statusMessage = Component.literal(
                            "§6[ChestESP] §fПодсветка хранилищ: " +
                            (enabled ? "§aВКЛЮЧЕНА" : "§cВЫКЛЮЧЕНА")
                    );
                    client.player.sendSystemMessage(statusMessage);
                }
            }
        });

        // 3. Register render callback via Fabric LevelRenderEvents
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
            if (!enabled) return;

            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            LocalPlayer player = client.player;
            if (level == null || player == null) return;

            Vec3 playerPos = player.position();
            int playerChunkX = player.getBlockX() >> 4;
            int playerChunkZ = player.getBlockZ() >> 4;
            int chunkRadius = (int) Math.ceil(MAX_RADIUS / 16.0); // 4 chunks around the player

            // 1. Scan block entities in loaded chunks surrounding the player
            for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; cx++) {
                for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; cz++) {
                    LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false);
                    if (chunk == null) continue;

                    for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                        BlockEntity blockEntity = entry.getValue();
                        BlockPos pos = entry.getKey();
                        double distSq = pos.distToCenterSqr(playerPos);
                        if (distSq > MAX_RADIUS_SQ) continue;

                        // Trapped chests (check before ChestBlockEntity as TrappedChestBlockEntity extends ChestBlockEntity)
                        if (blockEntity instanceof TrappedChestBlockEntity) {
                            AABB aabb = RenderUtils.getChestBoundingBox(level, pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.TRAPPED_CHEST);
                        }
                        // Regular chests
                        else if (blockEntity instanceof ChestBlockEntity) {
                            AABB aabb = RenderUtils.getChestBoundingBox(level, pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.CHEST);
                        }
                        // Ender chests
                        else if (blockEntity instanceof EnderChestBlockEntity) {
                            AABB aabb = RenderUtils.getEnderChestBoundingBox(pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.ENDER_CHEST);
                        }
                        // Shulker boxes of all colors (both dyed and undyed use ShulkerBoxBlockEntity)
                        else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                            AABB aabb = RenderUtils.getShulkerBoundingBox(pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.SHULKER_BOX);
                        }
                    }
                }
            }

            // 2. Scan entities for rendering — chest minecarts (MinecartChest)
            for (Entity entity : level.entitiesForRendering()) {
                if (entity instanceof MinecartChest minecart) {
                    if (minecart.position().distanceToSqr(playerPos) <= MAX_RADIUS_SQ) {
                        RenderUtils.renderGizmo(minecart.getBoundingBox(), ESPCategory.MINECART_CHEST);
                    }
                }
            }
        });
    }
}
