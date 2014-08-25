package mcp.mobius.pregen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import mcp.mobius.pregen.commands.CommandScanChunk;
import mcp.mobius.pregen.commands.CommandPregen;
import mcp.mobius.pregen.commands.CommandPurgeChunks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import mcp.mobius.pregen.commands.CommandWBPregen;

@Mod(modid="act", name="AdminCommandsToolbox", version="0.0.1")
public class AdminCommandsToolbox {
	@Instance("AdminCommandsToolbox")
	public static AdminCommandsToolbox instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //GameRegistry.registerWorldGenerator(new TestWorldGen(), 24);
 	}	
	
    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new TickHandler());
 	}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
	}
    
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandPregen());
		event.registerServerCommand(new CommandPurgeChunks());
        event.registerServerCommand(new CommandScanChunk());
        event.registerServerCommand(new CommandWBPregen());

		//TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
	}
}