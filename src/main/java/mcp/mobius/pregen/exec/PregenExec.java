package mcp.mobius.pregen.exec;

import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import java.util.*;

public class PregenExec extends BaseExec{

    public static PregenExec instance = new PregenExec();

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
    private int chunkLoadedCount = 0;

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
        chunkLoadedCount = provider.getLoadedChunkCount();
    }

    Set<ChunkCoordIntPair> processingChunks = new HashSet<ChunkCoordIntPair>();

    public void exec(){
        // notify about process
        if(System.currentTimeMillis()-lastMsg > 10000)
        {
            this.sendMsg(String.format("%.2f%% done (%d new chunks generated, %d processed)", 100f*(float)processedChunks/(float)totalCount, processedChunks-lastCount, processedChunks));
            lastMsg = System.currentTimeMillis();
            lastCount = processedChunks;
        }

        if(provider.getLoadedChunkCount() > 800)
        {
            this.sendMsg("New Chunks: " + provider.getLoadedChunkCount() + "  /  " + processingChunks.size());
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
                }

                provider.loadChunk(x,z);
                processingChunks.add(new ChunkCoordIntPair(x,z));
                processedChunks += 1;
                if (processedChunks % 100 == 0)
                    provider.unloadQueuedChunks();
                if (processedChunks % 1000 == 0)
                    doDump = true;
            }

            Iterator<ChunkCoordIntPair> iter =  processingChunks.iterator();
            while(iter.hasNext()) {
                ChunkCoordIntPair p = iter.next();
                if (provider.loadChunk(p.chunkXPos, p.chunkZPos).isTerrainPopulated) {
                    provider.unloadChunksIfNotNearSpawn(p.chunkXPos, p.chunkZPos);
                    iter.remove();
                }
                else
                    provider.populate(provider, p.chunkXPos, p.chunkZPos);
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
