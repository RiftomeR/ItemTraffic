package riftomer.itemtraffic.behaviours;

import buildcraft.api.inventory.IItemTransactor;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.lib.inventory.ItemTransactorHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import riftomer.itemtraffic.pipesystem.event.SortItemEvent;

public class PipeBehaviourDefaultRoute extends  PipeBehaviourBasic {
    public PipeBehaviourDefaultRoute(IPipe pipe) {
        super(pipe);
    }

    public PipeBehaviourDefaultRoute(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
    }
    @SubscribeEvent
    public void onItemSort(SortItemEvent event){
        for(EnumFacing facing:EnumFacing.values()){
            if(pipe.isConnected(facing)&&pipe.getConnectedType(facing)== IPipe.ConnectedType.TILE){
                TileEntity tileEntity = pipe.getConnectedTile(facing);
                IItemTransactor transactor = ItemTransactorHelper.getTransactor(tileEntity,facing.getOpposite());
                if(transactor.canFullyAccept(event.stack)) {
                    event.addPipe(this.pipe.getHolder().getPipePos(), -100);
                    return;
                }
            }
        }
    }
}
