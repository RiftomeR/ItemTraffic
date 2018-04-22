package riftomer.itemtraffic.pathfinding;

import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class PathFinders {
    private static Map<World, PathFinder> pathFinders = new HashMap<>();

    public static PathFinder getPathFinder(World world) {
        if (pathFinders.containsKey(world)) {
            return pathFinders.get(world);
        } else {
            PathFinder newPathFinder = new PathFinder(world);
            pathFinders.put(world, newPathFinder);
            return newPathFinder;
        }
    }
}
