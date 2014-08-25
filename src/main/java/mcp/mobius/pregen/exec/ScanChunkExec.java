package mcp.mobius.pregen.exec;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanChunkExec extends BaseExec {
    public static ScanChunkExec instance = new ScanChunkExec();

    public World world;
    public ChunkCoordinates coordinates;


    @Override
    public void exec() {
        Chunk chunk = world.getChunkFromBlockCoords(coordinates.posX, coordinates.posZ);

        this.sendMsg(world.getBiomeGenForCoords(coordinates.posX, coordinates.posY).biomeName);

        HashMap<Integer, Integer> oreCounts = new HashMap<Integer, Integer>();

        for(int y = 0; y < 256; y++)
            for(int x = 0; x < 16; x++)
                for(int z = 0; z < 16; z++)
                {
                    Block block = chunk.getBlock(x,y,z);
                    int meta = chunk.getBlockMetadata(x,y,z);
                    int id = OreDictionary.getOreID(new ItemStack(Item.getItemFromBlock(block), 1, meta));

                    if(!oreCounts.containsKey(id))
                        oreCounts.put(id, 0);

                    oreCounts.put(id, oreCounts.get(id) + 1);
                }

        for(Map.Entry<Integer, Integer> ore : oreCounts.entrySet())
            this.sendMsg(OreDictionary.getOreName(ore.getKey()) + ": " + ore.getValue());

        this.done();
    }
}
