package riftomer.itemtraffic.pathfinding;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class PathPart {
    public final EnumFacing facing;
    public final int cost;
    public final int fromStartCost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathPart pathPart = (PathPart) o;
        return cost == pathPart.cost &&
                fromStartCost == pathPart.fromStartCost &&
                facing == pathPart.facing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facing, cost, fromStartCost);
    }

    public PathPart(EnumFacing facing, int cost, int fromStartCost) {
        this.facing = facing;
        this.cost = cost;

        this.fromStartCost = fromStartCost;
    }

    public PathPart(BlockPos from, BlockPos to, EnumFacing facing, int fromStartCost) {
        this(facing, (int) Math.round(from.getDistance(to.getX(), to.getY(), to.getZ())) + fromStartCost, fromStartCost);
    }
}
