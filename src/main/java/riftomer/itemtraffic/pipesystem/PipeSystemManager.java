package riftomer.itemtraffic.pipesystem;

import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import riftomer.itemtraffic.ItemTraffic;
import riftomer.itemtraffic.behaviours.PipeBehaviourBasic;

@Mod.EventBusSubscriber
public class PipeSystemManager implements Runnable {
//    public static int delay = 5;
    public static boolean shouldStop = false;

//    @SubscribeEvent
//    public static void onServerTick(TickEvent.ServerTickEvent event){
//        delay++;
//        delay %= 20;
//    }

    @Override
    public void run() {
        ItemTraffic.logger.info("Starting pipe system management thread");
        while(!shouldStop) {
            try {
                for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                    for (Chunk chunk : world.getChunkProvider().getLoadedChunks()) {
                        for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
                            if (tileEntity instanceof TilePipeHolder) {
                                TilePipeHolder holder = (TilePipeHolder) tileEntity;
                                if (holder.getPipe().behaviour instanceof PipeBehaviourBasic) {
                                    PipeBehaviourBasic behaviour = (PipeBehaviourBasic) holder.getPipe().behaviour;
                                    behaviour.managePipeSystem();
                                }
                            }
                        }
                    }
                }
            }catch (Exception ex){
                ItemTraffic.logger.error("Error in pipe system management thread",ex);
            }
        }
        ItemTraffic.logger.info("Stopping pipe system management thread");
        shouldStop = false;
    }
}
