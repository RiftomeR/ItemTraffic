package riftomer.itemtraffic.pipesystem.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import riftomer.itemtraffic.behaviours.PipeBehaviourBasic;

public class SortItemEvent extends PipeSystemEvent {
    public final ItemStack stack;
    private BlockPos pos;
    private int bestPriority = 0;
    private boolean bestPriorityFilled = false;

    public SortItemEvent(PipeBehaviourBasic behaviour, ItemStack stack) {
        super(behaviour);
        this.stack = stack;
    }

    public BlockPos getPipePos(){
        return pos;
    }

    public void addPipe(BlockPos pos, int priority){
        if(bestPriorityFilled){
            if(priority>bestPriority){
                this.pos = pos;
                this.bestPriority = priority;
            }
        }else{
            bestPriority = priority;
            this.pos = pos;
            bestPriorityFilled = true;
        }
    }
}
