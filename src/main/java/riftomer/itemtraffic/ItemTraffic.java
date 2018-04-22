package riftomer.itemtraffic;

import buildcraft.lib.BCLib;
import buildcraft.transport.BCTransport;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import riftomer.itemtraffic.pipesystem.PipeSystemManager;
import riftomer.itemtraffic.proxies.CommonProxy;

@Mod(modid = Reference.MODID, dependencies = "required-after:" + BCTransport.MODID + "@[" + BCLib.VERSION + "]")
public class ItemTraffic {
    @SidedProxy(clientSide = "riftomer.itemtraffic.proxies.ClientProxy", serverSide = "riftomer.itemtraffic.proxies.CommonProxy", modId = Reference.MODID)
    public static CommonProxy PROXY;
    public static Logger logger;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        new Thread(new PipeSystemManager()).start();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event){
        PipeSystemManager.shouldStop = true;
    }
}
