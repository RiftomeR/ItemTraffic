package riftomer.itemtraffic.proxies;

import buildcraft.api.transport.pipe.PipeApi;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.lib.registry.CreativeTabManager;
import buildcraft.lib.registry.TagManager;
import buildcraft.transport.pipe.PipeRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import riftomer.itemtraffic.Reference;
import riftomer.itemtraffic.behaviours.PipeBehaviourBasic;
import riftomer.itemtraffic.behaviours.PipeBehaviourDefaultRoute;
import riftomer.itemtraffic.init.Items;
import riftomer.itemtraffic.init.Pipes;

@Mod.EventBusSubscriber()
public class CommonProxy {
    public static CreativeTabManager.CreativeTabBC tabItemtraffic;

    public void preInit(FMLPreInitializationEvent event) {
        tabItemtraffic = CreativeTabManager.createTab("itemtraffic.main");
        manageTags();
        registerPipes();
        tabItemtraffic.setItem(Items.pipeBasic);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {

    }

    private void manageTags() {
        TagManager.startBatch();
        TagManager.registerTag("item.pipe." + Reference.MODID + ".basic").reg("pipe_basic").locale("PipeBasic");
        TagManager.registerTag("item.pipe." + Reference.MODID + ".default_route").reg("pipe_default_route").locale("PipeDefaultRoute");
        TagManager.endBatch(TagManager.setTab("itemtraffic.main"));
    }

    private void registerPipes() {
        Pipes.basicPipe = new PipeDefinition.PipeDefinitionBuilder().idTex("basic").flow(PipeApi.flowItems).logic(PipeBehaviourBasic::new, PipeBehaviourBasic::new).define();
        Items.pipeBasic = PipeRegistry.INSTANCE.createItemForPipe(Pipes.basicPipe);
        Pipes.defaultRoutePipe = new PipeDefinition.PipeDefinitionBuilder().idTex("default_route").flow(PipeApi.flowItems).logic(PipeBehaviourDefaultRoute::new, PipeBehaviourDefaultRoute::new).define();
        Items.pipeDefaultRoute = PipeRegistry.INSTANCE.createItemForPipe(Pipes.defaultRoutePipe);
    }
}
