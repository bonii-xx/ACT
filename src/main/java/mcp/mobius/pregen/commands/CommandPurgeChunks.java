package mcp.mobius.pregen.commands;

import mcp.mobius.pregen.exec.PurgeChunks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.gen.ChunkProviderServer;

public class CommandPurgeChunks extends CommandBase {

	@Override
	public String getCommandName() {
		return "purgechunks";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/purgechunks : This command will try to free as many chunks as possible.";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		PurgeChunks.instance.addMsgTarget(icommandsender);
		PurgeChunks.instance.run();
	}

	@Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender){
		//if ((sender instanceof EntityPlayerMP) && ((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;
		return super.canCommandSenderUseCommand(sender);
    }	
}
