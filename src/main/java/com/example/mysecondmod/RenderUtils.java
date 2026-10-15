package com.example.mysecondmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;

/**
 * Утилитарный класс для рендеринга AABB-объемов сквозь препятствия в Minecraft 26.3 с использованием Gizmos API.
 */
public class RenderUtils {

    /**
     * Отрисовывает подсветку AABB-объема сквозь препятствия с цветным контуром и полупрозрачным заполнением.
     *
     * @param aabb     ограничивающий объем (bounding box) в мировых координатах
     * @param category категория объекта с соответствующими цветами
     */
    public static void renderGizmo(AABB aabb, ESPCategory category) {
        float strokeWidth = 2.0f;

        // GizmoStyle объединяет контурные линии и прозрачные грани,
        // а setAlwaysOnTop() отключает тест глубины, делая бокс видимым сквозь стены.
        Gizmos.cuboid(aabb, GizmoStyle.strokeAndFill(category.getStrokeColor(), strokeWidth, category.getFillColor()))
              .setAlwaysOnTop();
    }

    /**
     * Вычисляет точный AABB-объем сундука (обычного или сундука-ловушки) в мировых координатах
     * с учетом одинарных и соединенных двойных сундуков.
     */
    public static AABB getChestBoundingBox(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ChestBlock) {
            double minX = 0.0625;
            double minY = 0.0;
            double minZ = 0.0625;
            double maxX = 0.9375;
            double maxY = 0.875;
            double maxZ = 0.9375;

            if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                Direction connected = ChestBlock.getConnectedDirection(state);
                if (connected == Direction.WEST) minX = 0.0;
                else if (connected == Direction.EAST) maxX = 1.0;
                else if (connected == Direction.NORTH) minZ = 0.0;
                else if (connected == Direction.SOUTH) maxZ = 1.0;
            }

            return new AABB(
                    pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ,
                    pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ
            );
        }

        return getEnderChestBoundingBox(pos);
    }

    /**
     * Вычисляет AABB-объем эндер-сундука (всегда одинарный сундук).
     */
    public static AABB getEnderChestBoundingBox(BlockPos pos) {
        return new AABB(
                pos.getX() + 0.0625, pos.getY(), pos.getZ() + 0.0625,
                pos.getX() + 0.9375, pos.getY() + 0.875, pos.getZ() + 0.9375
        );
    }

    /**
     * Вычисляет AABB-объем ящика шалкера (полный кубический блок 1x1x1).
     */
    public static AABB getShulkerBoundingBox(BlockPos pos) {
        return new AABB(pos);
    }
}
