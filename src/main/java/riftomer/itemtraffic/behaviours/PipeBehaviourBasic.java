package riftomer.itemtraffic.behaviours;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import riftomer.itemtraffic.ItemTraffic;
import riftomer.itemtraffic.pathfinding.PathFinder;
import riftomer.itemtraffic.pathfinding.PathFinders;
import riftomer.itemtraffic.pathfinding.PathFindingResult;
import riftomer.itemtraffic.pipesystem.PipeSystem;
import riftomer.itemtraffic.pipesystem.event.SortItemEvent;
import riftomer.itemtraffic.util.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PipeBehaviourBasic extends PipeBehaviour {
    public static PathFinder pathFinder;

    public Map<ItemStack, PathFindingResult> exceptedItems;

    public PipeSystem system;
    private boolean systemChecked = false;

    public PipeBehaviourBasic(IPipe pipe) {
        super(pipe);
        pathFinder = PathFinders.getPathFinder(pipe.getHolder().getPipeWorld());
        exceptedItems = new TreeMap<>((o1, o2) -> {
            if (ItemStack.areItemStacksEqual(o1, o2)) {
                return 0;
            }
            return 1;
        });

    }

    public PipeBehaviourBasic(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
    }


    @Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) {
        if (!player.world.isRemote) {
            player.sendStatusMessage(new TextComponentString("Pipe system id is " + system.id), false);
        }
        return true;
    }

    @Override
    public void onTick() {
        super.onTick();
//        if(PipeSystemManager.delay == 0 && !pipe.getHolder().getPipeWorld().isRemote) {
//            managePipeSystem();
//        }
    }

    public void managePipeSystem() {
        if (systemChecked) {
            systemChecked = false;
        } else {
            if (system == null) {
                system = new PipeSystem();
            } else {
                try {
                    pathFinder.findPath(this.pipe.getHolder().getPipePos(), system.masterBlockPos);
                } catch (IllegalArgumentException ex) {
                    system = new PipeSystem();
                }
            }
            system.masterBlockPos = pipe.getHolder().getPipePos();
            spreadPipeSystem();
        }
    }

    private void spreadPipeSystem() {
        //Prepare everything
        List<BlockPos> unchecked = new ArrayList<>();
        unchecked.add(this.pipe.getHolder().getPipePos());
        List<BlockPos> checked = new ArrayList<>();

        //Check every pipe in system
        while (!unchecked.isEmpty()) {
            BlockPos toCheck = unchecked.get(0);
            unchecked.remove(0);
            checked.add(toCheck);
            if (pipe.getHolder().getPipeWorld().getTileEntity(toCheck) instanceof TilePipeHolder) {
                TilePipeHolder tileEntity = (TilePipeHolder) pipe.getHolder().getPipeWorld().getTileEntity(toCheck);
                if (tileEntity != null) {
                    //Modify pipe system of target pipe
                    if (tileEntity.getPipe().behaviour instanceof PipeBehaviourBasic /*&& (!toCheck.equals(this.pipe.getHolder().getPipePos()))*/) {
                        ((PipeBehaviourBasic) tileEntity.getPipe().behaviour).setPipeSystem(this.system);
                    }

                    //Check near pipes
                    for (EnumFacing facing : EnumFacing.values()) {
                        BlockPos pos = toCheck.offset(facing);
                        if ((!checked.contains(pos)) && (pipe.getHolder().getPipeWorld().getTileEntity(toCheck) instanceof TilePipeHolder)) {
                            unchecked.add(pos);
                        }
                    }
                }
            }
        }
    }


    private void setPipeSystem(PipeSystem system) {
        if (this.system != null) {
            try {
                this.system.eventBus.unregister(this);
            } catch (IllegalArgumentException ex) {
                //Pipe is not registered
            }
        }
        this.system = system;
        this.system.eventBus.register(this);
        systemChecked = true;
    }


    @PipeEventHandler
    public void checkSides(PipeEventItem.SideCheck sideCheck) {
        if (!pipe.getHolder().getPipeWorld().isRemote) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (pipe.getConnectedType(facing) == IPipe.ConnectedType.TILE) {
                    sideCheck.increasePriority(facing, 1000);
                }
            }
            if (!Maps.containsItemStackKey(sideCheck.stack, exceptedItems)) {
                SortItemEvent event = new SortItemEvent(this, sideCheck.stack);
                system.eventBus.post(event);
                if (event.getPipePos() == null) {
                    sideCheck.disallowAll();
                    return;
                } else {
                    sendItemToPipe(event.getPipePos(), sideCheck.stack);
                }
            }
            PathFindingResult pathFindingResult = Maps.getPFRWithItemStackKey(sideCheck.stack, exceptedItems);
            if (!pathFindingResult.isPipeLastest()) {
                sideCheck.disallowAllExcept(pathFindingResult.getDirection());
                PipeBehaviourBasic pipe = ((PipeBehaviourBasic) pathFindingResult.getNextSortingPipe().getPipe().behaviour);
                pipe.exceptedItems.put(sideCheck.stack, pathFindingResult);
                ItemTraffic.logger.info(exceptedItems.size());
            }
            Maps.removeStackFromMap(sideCheck.stack, exceptedItems);
            ItemTraffic.logger.info("Left: ");
            ItemTraffic.logger.info(exceptedItems.size());
        }

    }

    protected void sendItemToPipe(BlockPos to, ItemStack stack) {
        exceptedItems.put(stack, pathFinder.findPath(pipe.getHolder().getPipePos(), to));
    }
}
