package riftomer.itemtraffic.pathfinding;

import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riftomer.itemtraffic.behaviours.PipeBehaviourBasic;

import java.util.*;

public class PathFindingResult {
    public final List<Map.Entry<BlockPos, PathPart>> path;

    private int lastSortingPipe = 0;

    public final World world;

    public PathFindingResult(List<Map.Entry<BlockPos, PathPart>> path, World world) {
        this.path = path;
        this.world = world;
    }

    public PathFindingResult(NBTTagCompound compound, World world) {
        this(new ArrayList<>(), world);
        NBTTagCompound path = compound.getCompoundTag("path");
        NBTTagList keys = path.getTagList("keys", compound.getInteger("keyType"));
        NBTTagList values = path.getTagList("values", compound.getInteger("valueType"));
        for (int i = 0; i < keys.tagCount(); i++) {
            NBTTagCompound posCompound = keys.getCompoundTagAt(i);
            EnumFacing facing = EnumFacing.values()[values.getIntAt(i)];
            BlockPos pos = new BlockPos(posCompound.getInteger("x"), posCompound.getInteger("y"), posCompound.getInteger("z"));
            this.path.add(new AbstractMap.SimpleEntry<>(pos, new PathPart(facing, 0, 0)));
        }
        lastSortingPipe = compound.getInteger("lastSortingPipe");
    }

    public TilePipeHolder getNextSortingPipe() {
        lastSortingPipe++;
        while (lastSortingPipe < path.size()) {
            TilePipeHolder pipeHolder = (TilePipeHolder) world.getTileEntity(path.get(lastSortingPipe).getKey());
            if (pipeHolder.getPipe().behaviour instanceof PipeBehaviourBasic) {
                return pipeHolder;
            }
            lastSortingPipe++;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathFindingResult result = (PathFindingResult) o;
        return lastSortingPipe == result.lastSortingPipe &&
                Objects.equals(path, result.path) &&
                Objects.equals(world, result.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, lastSortingPipe, world);
    }

    public EnumFacing getDirection() {
        return path.get(lastSortingPipe + 1).getValue().facing.getOpposite();
    }

    public void reset() {
        lastSortingPipe = 0;
    }

    public boolean isPipeLastest() {
        return lastSortingPipe == (path.size() - 1);
    }

    public boolean isPipeFirst() {
        return lastSortingPipe == 0;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound result = new NBTTagCompound();
        NBTTagCompound path = new NBTTagCompound();
        NBTTagList keys = new NBTTagList();
        NBTTagList values = new NBTTagList();
        for (Map.Entry<BlockPos, PathPart> next : this.path) {
            NBTTagCompound pos = new NBTTagCompound();
            pos.setInteger("x", next.getKey().getX());
            pos.setInteger("y", next.getKey().getY());
            pos.setInteger("z", next.getKey().getZ());
            keys.appendTag(pos);
            values.appendTag(new NBTTagInt(next.getValue().facing.ordinal()));

        }
        path.setTag("keys", keys);
        path.setTag("values", values);
        result.setTag("path", path);
        result.setInteger("lastSortingPipe", lastSortingPipe);
        return result;
    }
}
