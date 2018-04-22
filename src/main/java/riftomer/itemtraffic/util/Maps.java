package riftomer.itemtraffic.util;

import net.minecraft.item.ItemStack;
import riftomer.itemtraffic.pathfinding.PathFindingResult;

import java.util.Map;

public class Maps {
    public static boolean containsItemStackKey (ItemStack stack, Map<ItemStack,PathFindingResult> map){
        for (ItemStack mapStack:map.keySet()) {
            if(ItemStack.areItemStacksEqual(stack,mapStack)){
                return true;
            }
        }
        return false;
    }
    public static PathFindingResult getPFRWithItemStackKey(ItemStack stack, Map<ItemStack,PathFindingResult> map){
        for (ItemStack mapStack:map.keySet()) {
            if(ItemStack.areItemStacksEqual(stack,mapStack)){
                return map.get(mapStack);
            }
        }
        return null;
    }

    public static void removeStackFromMap(ItemStack stack, Map<ItemStack,PathFindingResult> map){
        for (ItemStack mapStack:map.keySet()) {
            if(ItemStack.areItemStacksEqual(stack,mapStack)){
                map.remove(mapStack);
            }
        }
    }
}
