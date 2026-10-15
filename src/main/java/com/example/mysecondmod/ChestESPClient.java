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
 * Точка входа клиентского мода ChestESP (v2.0) для Fabric 26.3.
 * Расширенный ChestESP с подсветкой сундуков, сундуков-ловушек, эндер-сундуков, всех цветов шалкеров и вагонеток с сундуком сквозь препятствия.
 */
public class ChestESPClient implements ClientModInitializer {

    public static final String MOD_ID = "chestesp";

    /** Горячая клавиша переключения ESP (по умолчанию клавиша 'V') */
    private static KeyMapping toggleKey;

    /** Текущее состояние работы ESP */
    private static boolean enabled = true;

    /** Максимальный радиус поиска (64 блока) */
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
        // 1. Регистрация бинда клавиши в категории MISC
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.chestesp.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_V,
                KeyMapping.Category.MISC
        ));

        // 2. Обработка нажатия клавиши в цикле тика клиента
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

        // 3. Подписка на рендеринг через Fabric LevelRenderEvents
        LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
            if (!enabled) return;

            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            LocalPlayer player = client.player;
            if (level == null || player == null) return;

            Vec3 playerPos = player.position();
            int playerChunkX = player.getBlockX() >> 4;
            int playerChunkZ = player.getBlockZ() >> 4;
            int chunkRadius = (int) Math.ceil(MAX_RADIUS / 16.0); // 4 чанка вокруг игрока

            // 1. Сканируем BlockEntity в загруженных чанках вокруг игрока
            for (int cx = playerChunkX - chunkRadius; cx <= playerChunkX + chunkRadius; cx++) {
                for (int cz = playerChunkZ - chunkRadius; cz <= playerChunkZ + chunkRadius; cz++) {
                    LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false);
                    if (chunk == null) continue;

                    for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                        BlockEntity blockEntity = entry.getValue();
                        BlockPos pos = entry.getKey();
                        double distSq = pos.distToCenterSqr(playerPos);
                        if (distSq > MAX_RADIUS_SQ) continue;

                        // Сундуки-ловушки (проверяем перед ChestBlockEntity, так как TrappedChestBlockEntity наследует ChestBlockEntity)
                        if (blockEntity instanceof TrappedChestBlockEntity) {
                            AABB aabb = RenderUtils.getChestBoundingBox(level, pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.TRAPPED_CHEST);
                        }
                        // Обычные сундуки
                        else if (blockEntity instanceof ChestBlockEntity) {
                            AABB aabb = RenderUtils.getChestBoundingBox(level, pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.CHEST);
                        }
                        // Эндер-сундуки
                        else if (blockEntity instanceof EnderChestBlockEntity) {
                            AABB aabb = RenderUtils.getEnderChestBoundingBox(pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.ENDER_CHEST);
                        }
                        // Шалкеры всех цветов (все окрашенные и неокрашенные используют ShulkerBoxBlockEntity)
                        else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                            AABB aabb = RenderUtils.getShulkerBoundingBox(pos);
                            RenderUtils.renderGizmo(aabb, ESPCategory.SHULKER_BOX);
                        }
                    }
                }
            }

            // 2. Сканируем сущности (Entity) — вагонетки с сундуком (ChestMinecartEntity / MinecartChest)
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
