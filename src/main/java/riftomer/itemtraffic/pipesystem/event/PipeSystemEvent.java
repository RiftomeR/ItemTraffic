package riftomer.itemtraffic.pipesystem.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import riftomer.itemtraffic.behaviours.PipeBehaviourBasic;

public class PipeSystemEvent extends Event {
    public final PipeBehaviourBasic behaviour;

    public PipeSystemEvent(PipeBehaviourBasic behaviour) {
        this.behaviour = behaviour;
    }
}
