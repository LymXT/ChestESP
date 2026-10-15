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
 * Utility class for rendering AABB volumes through obstacles in Minecraft 26.3 using the Gizmos API.
 */
public class RenderUtils {

    /**
     * Renders an AABB highlight through obstacles with a colored outline and translucent face fill.
     *
     * @param aabb     the bounding box in world coordinates
     * @param category the object category defining outline and fill colors
     */
    public static void renderGizmo(AABB aabb, ESPCategory category) {
        float strokeWidth = 2.0f;

        // GizmoStyle combines outline strokes and translucent face fills,
        // while setAlwaysOnTop() disables depth testing to ensure through-wall visibility.
        Gizmos.cuboid(aabb, GizmoStyle.strokeAndFill(category.getStrokeColor(), strokeWidth, category.getFillColor()))
              .setAlwaysOnTop();
    }

    /**
     * Calculates the exact AABB bounding box of a chest (regular or trapped) in world coordinates,
     * accounting for single and connected double chests.
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
     * Calculates the AABB bounding box of an ender chest (always a single chest volume).
     */
    public static AABB getEnderChestBoundingBox(BlockPos pos) {
        return new AABB(
                pos.getX() + 0.0625, pos.getY(), pos.getZ() + 0.0625,
                pos.getX() + 0.9375, pos.getY() + 0.875, pos.getZ() + 0.9375
        );
    }

    /**
     * Calculates the AABB bounding box of a shulker box (full 1x1x1 cube block).
     */
    public static AABB getShulkerBoundingBox(BlockPos pos) {
        return new AABB(pos);
    }
}
