package mcp.mobius.pregen.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import mcp.mobius.pregen.exec.PregenExec;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class CommandPregen extends CommandBase {

	@Override
	public String getCommandName(){	return "pregen"; }

	@Override
	public String getCommandUsage(ICommandSender icommandsender){ 
		return "/pregen <dim> <minX> <maxX> <minZ> <maxZ>\nGenerate a portion of world between chunk coordinates [minX,maxX] and [minZ,maxZ]"; 
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring){
		if (astring.length != 1 && astring.length !=4 && astring.length != 5){
            icommandsender.addChatMessage(new ChatComponentText("pregen <blockradius>"));
            icommandsender.addChatMessage(new ChatComponentText("pregen <dim> <blockX> <blockY> <blockradius>"));
            icommandsender.addChatMessage(new ChatComponentText("Generate all chunks inside the blockradius"));
            icommandsender.addChatMessage(new ChatComponentText("pregen <dim> <minX> <maxX> <minZ> <maxZ>"));
            icommandsender.addChatMessage(new ChatComponentText("Generate a portion of world between chunk coordinates [minX,maxX] and [minZ,maxZ]"));
			return;
		}

        PregenExec.instance.addMsgTarget(icommandsender);
        if(astring.length == 1)
        {
            PregenExec.instance.dim = 0;
            for(int i : DimensionManager.getIDs())
                if(DimensionManager.getWorld(i) == icommandsender.getEntityWorld())
                    PregenExec.instance.dim = i;

            int x = icommandsender.getPlayerCoordinates().posX/16;
            int z = icommandsender.getPlayerCoordinates().posZ/16;
            int r = Integer.valueOf(astring[0])/16+1;

            PregenExec.instance.minX = x-r;
            PregenExec.instance.maxX = x+r;
            PregenExec.instance.minZ = z-r;
            PregenExec.instance.maxZ = z+r;
        }
        else
            PregenExec.instance.dim    = Integer.valueOf(astring[0]);

        if(astring.length == 4)
        {
            int x = Integer.valueOf(astring[1])/16;
            int z = Integer.valueOf(astring[2])/16;
            int r = Integer.valueOf(astring[3])/16+1;

            PregenExec.instance.minX = x-r;
            PregenExec.instance.maxX = x+r;
            PregenExec.instance.minZ = z-r;
            PregenExec.instance.maxZ = z+r;
        }
        if(astring.length == 5) {
            PregenExec.instance.minX = Math.min(Integer.valueOf(astring[1]), Integer.valueOf(astring[2]));
            PregenExec.instance.maxX = Math.max(Integer.valueOf(astring[1]), Integer.valueOf(astring[2]));
            PregenExec.instance.minZ = Math.min(Integer.valueOf(astring[3]), Integer.valueOf(astring[4]));
            PregenExec.instance.maxZ = Math.max(Integer.valueOf(astring[3]), Integer.valueOf(astring[4]));
        }
		PregenExec.instance.run();
	}

	@Override
    public int getRequiredPermissionLevel(){ return 3; }	

	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender){
		//if ((sender instanceof EntityPlayerMP) && ((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;
		return super.canCommandSenderUseCommand(sender);
    }
	
	
}
