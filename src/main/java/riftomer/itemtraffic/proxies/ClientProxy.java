package riftomer.itemtraffic.proxies;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import riftomer.itemtraffic.Reference;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class ClientProxy extends CommonProxy {

}
