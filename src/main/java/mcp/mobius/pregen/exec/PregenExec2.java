package mcp.mobius.pregen.exec;

import java.util.*;

import com.google.common.collect.ImmutableSetMultimap;

import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class PregenExec2 extends BaseExec{
	
	public static PregenExec2 instance = new PregenExec2();
	
	public int dim  = 0;
	public int minX = 0;
	public int maxX = 0;
	public int minZ = 0;
	public int maxZ = 0;

    private int processedChunks = 0;
    private int totalCount = 0;
    ChunkProviderServer provider = null;
    Queue<Chunk> loadedChunks = new LinkedList<Chunk>();
    ImmutableSetMultimap<ChunkCoordIntPair, Ticket> forcedChunks;

    private int x = 0;
    private int z = 0;

    private int cpt = 12; // chunks per tick

    private long lastMsg = 0;
    private int lastCount = 0;

    @Override
    public void run() {
        super.run();

        processedChunks = 0;
        totalCount = (maxX-minX)*(maxZ-minZ);

        World world = DimensionManager.getWorld(dim);

        if (world == null){
            this.sendMsg("Pregen : can't find dimension.");
            this.shouldRun = false;
            return;
        }

        provider = (ChunkProviderServer)world.getChunkProvider();
        forcedChunks = ForgeChunkManager.getPersistentChunksFor(world);


        this.sendMsg(String.format("Pregenerating for dim %d | X : [ %d %d ] | Z : [ %d %d ]", dim, minX, maxX, minZ, maxZ));
        this.sendMsg(String.format("Chunks to generate: %d", totalCount));
        lastMsg = System.currentTimeMillis();

        x = maxX;
        z = minZ-1;
        lastCount = 0;
    }


    public void exec(){
        // notify about process
        if(System.currentTimeMillis()-lastMsg > 10000)
        {
            this.sendMsg(String.format("%.2f%% done (%d new chunks generated, %d processed)", 100f*(float)processedChunks/(float)totalCount, processedChunks-lastCount, processedChunks));
            lastMsg = System.currentTimeMillis();
            lastCount = processedChunks;
        }

        boolean doDump = false;

        try{

            int i = 0;
            while(i++ < cpt)
            {
                // go to next column
                x++;

                // row end?
                if(x > maxX)
                {
                    x = minX;
                    z++;
                    if(z > maxZ) {
                        // we're done. x and z both past max :)
                        this.done();
                        return;
                    }

                    // load the 9 chunks surrounding the first chunk in this row
                    loadedChunks.add(provider.loadChunk(x-1, minZ-1)); // these 4
                    loadedChunks.add(provider.loadChunk(x,   minZ-1)); // chunks are
                    loadedChunks.add(provider.loadChunk(x+1, minZ-1)); // actually outside
                    loadedChunks.add(provider.loadChunk(x-1, minZ));   // the border
                    loadedChunks.add(provider.loadChunk(x,   minZ));
                    loadedChunks.add(provider.loadChunk(x+1, minZ));

                    processedChunks += 1;
                    if (processedChunks % 1000 == 0)
                        doDump = true;
                }

                // load the next chunk
                loadedChunks.add(provider.loadChunk(x-1, z+1));
                loadedChunks.add(provider.loadChunk(x,   z+1));
                loadedChunks.add(provider.loadChunk(x+1, z+1));

                provider.populate(provider, x, z);
                processedChunks += 1;
                if (processedChunks % 1000 == 0)
                    doDump = true;

                // unload old chunks
                while(loadedChunks.size() > 12)
                {
                    Chunk chunk = loadedChunks.poll();
                    provider.safeSaveExtraChunkData(chunk);
                    provider.safeSaveChunk(chunk);
                    if(!forcedChunks.containsKey(new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition)))
                        provider.unloadChunksIfNotNearSpawn(chunk.xPosition, chunk.zPosition);
                }
            }
		} catch (Exception e) {
			this.sendMsg(String.format("%s", e));
            e.printStackTrace();
		}

        // do a cleanup every 100 chunks or so
        if (doDump) {
            this.sendMsg(String.format("Dumping chunk data..."));
            provider.unloadQueuedChunks();
            provider.saveChunks(true, null);
        }
	}

    @Override
    public void done() {
        // do a save at the end
        provider.unloadQueuedChunks();
        try{
            new CommandSaveAll().processCommand(this.firstTarget, new String[]{"flush"});
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sendMsg("Pregen : generation done");

        super.done();
    }

    private boolean isMemory(ChunkProviderServer provider, int par1, int par2){
        long k = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
        return provider.loadedChunkHashMap.containsItem(k);
	}

	private boolean isDisk(ChunkProviderServer provider, int par1, int par2){
        try{
            Chunk chunk = provider.currentChunkLoader.loadChunk(provider.worldObj, par1, par2);
            if (chunk != null)
            	return true;
        }
        catch (Exception exception){
            exception.printStackTrace();
            return true;
        }		
		
		return false;
	}
}
