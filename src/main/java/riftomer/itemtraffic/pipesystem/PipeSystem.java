package riftomer.itemtraffic.pipesystem;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class PipeSystem {
    public BlockPos masterBlockPos;
    public final EventBus eventBus;
    private static int lastId = 0;
    public final int id;

    public PipeSystem() {
        this.eventBus = new EventBus();
        id = lastId++;
    }
}
