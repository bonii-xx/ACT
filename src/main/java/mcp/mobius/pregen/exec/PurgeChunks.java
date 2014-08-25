package mcp.mobius.pregen.exec;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class PurgeChunks extends BaseExec{

	public static PurgeChunks instance = new PurgeChunks();
	
	public void exec(){
		int loadedChunks = 0;
		
		for (WorldServer world : DimensionManager.getWorlds()){
			loadedChunks += world.getChunkProvider().getLoadedChunkCount();
		}

		this.sendMsg(String.format("Loaded chunks before : %d\n", loadedChunks));
		
		for (WorldServer world : DimensionManager.getWorlds()){
			
			int loadedChunksDelta = 100;
			
			while(loadedChunksDelta >= 100){
				int loadedBefore = world.getChunkProvider().getLoadedChunkCount();
				((ChunkProviderServer)world.getChunkProvider()).unloadAllChunks();
				world.getChunkProvider().unloadQueuedChunks();
				loadedChunksDelta = loadedBefore - world.getChunkProvider().getLoadedChunkCount();
			}
		}

		loadedChunks = 0;
		for (WorldServer world : DimensionManager.getWorlds()){
			loadedChunks += world.getChunkProvider().getLoadedChunkCount();
		}
		
		this.sendMsg(String.format("Loaded chunks after : %d\n", loadedChunks));
		
		this.done();
	}
}
