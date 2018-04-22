package riftomer.itemtraffic.pathfinding;

import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class PathFinder {
    private final World world;

    public PathFinder(World world) {
        this.world = world;
    }

    public PathFindingResult findPath(BlockPos start, BlockPos destination) {
        //Prepare everything
        Map<BlockPos, PathPart> unchecked = new TreeMap<>();
        unchecked.put(start, new PathPart(null, 0, 0));
        Map<BlockPos, PathPart> checked = new TreeMap<>();

        //Check path to destination
        while (true) {
            boolean found = false;
            Map<BlockPos, PathPart> checking = getAndRemoveWithLowestCost(unchecked);
            //Check all blocks with lowest cost
            for (Map.Entry<BlockPos, PathPart> entry : checking.entrySet()) {
                unchecked.remove(entry.getKey());
                checked.put(entry.getKey(), entry.getValue());
                //Test if destination is reached
                if (entry.getKey().equals(destination)){
                    found = true;
                } else {
                    //Add new blocks to unchecked
                    for (EnumFacing facing : EnumFacing.values()) {
                        BlockPos pos = entry.getKey().offset(facing);
                        if (world.getTileEntity(pos) instanceof TilePipeHolder) {
                            //Test if current block is already checked
                            if (!checked.containsKey(pos)) {
                                PathPart newPathPart = new PathPart(pos, destination, facing.getOpposite(), entry.getValue().fromStartCost + 1);
                                if (unchecked.containsKey(pos)) {
                                    //If previous path part has lesser cost then current, it will not be overwritenn by current path part
                                    if (unchecked.get(pos).cost < newPathPart.cost) {
                                        unchecked.put(pos, newPathPart);
                                    }
                                } else {
                                    unchecked.put(pos, new PathPart(pos, destination, facing.getOpposite(), entry.getValue().fromStartCost + 1));
                                }
                            }
                        }
                    }
                }
            }

            if (found) {
                break;
            } else {
                //If there are no unchecked blocks left and destination is not reached, then destination is unreachable and the exception will be thrown
                if (unchecked.isEmpty()) {
                    throw new IllegalArgumentException("Destination is unreachable");
                }
            }
        }

        //Generate backwards path
        List<Map.Entry<BlockPos, PathPart>> backwardsPath = new ArrayList<>();
        Map.Entry<BlockPos, PathPart> curentPathPart = new AbstractMap.SimpleEntry<>(destination, checked.get(destination));
        while (true) {
            backwardsPath.add(curentPathPart);
            if (curentPathPart.getValue().facing == null) {
                break;
            } else {
                BlockPos curBlockPos = curentPathPart.getKey().offset(curentPathPart.getValue().facing);
                curentPathPart = new AbstractMap.SimpleEntry<>(curBlockPos, checked.get(curBlockPos));
            }
        }

        //Reverse path
        List<Map.Entry<BlockPos, PathPart>> result = new ArrayList<>();
        for (int i = backwardsPath.size() - 1; i >= 0; i--) {
            result.add(backwardsPath.get(i));
        }
        return new PathFindingResult(result, world);
    }

    private static Map<BlockPos, PathPart> getAndRemoveWithLowestCost(Map<BlockPos, PathPart> from) {
        //Prepare everything
        int lowestCost = 0;
        boolean filled = false;
        Map<BlockPos, PathPart> result = new TreeMap<>();

        //Fill result
        for (Map.Entry<BlockPos, PathPart> entry : from.entrySet()) {
            //Test if lowest cost is not filled
            if (filled) {
                if (entry.getValue().cost == lowestCost) {
                    result.put(entry.getKey(), entry.getValue());
                } else if (entry.getValue().cost < lowestCost) {
                    lowestCost = entry.getValue().cost;
                    result.clear();
                    result.put(entry.getKey(), entry.getValue());
                }
            } else {
                lowestCost = entry.getValue().cost;
                result.put(entry.getKey(),entry.getValue());
                filled = true;
            }
        }

        //Remove result from map
        for (BlockPos pos : result.keySet()) {
            from.remove(pos);
        }

        return result;
    }
}
