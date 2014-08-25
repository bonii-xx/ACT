package mcp.mobius.pregen.commands;

import mcp.mobius.pregen.exec.BaseExec;
import mcp.mobius.pregen.exec.PregenExec;
import mcp.mobius.pregen.exec.WBPregenExec;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class CommandWBPregen extends CommandBase {

	@Override
	public String getCommandName(){	return "wbpregen"; }

	@Override
	public String getCommandUsage(ICommandSender icommandsender){ 
		return "/pregen <dim> <X> <X> <radius>\nGenerate a portion of world between chunk coordinates [minX,maxX] and [minZ,maxZ]";
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring){
		if (astring.length < 4){
            icommandsender.addChatMessage(new ChatComponentText("pregen <dim> <X> <Z> <radius>"));
            icommandsender.addChatMessage(new ChatComponentText("x, Z are block coordinates. Radius is the width/height of the amount to generate in blocks."));
			return;
		}

        World world = DimensionManager.getWorld(Integer.valueOf(astring[0]));
        int x = Integer.valueOf(astring[1]);
        int z = Integer.valueOf(astring[2]);
        int r = Integer.valueOf(astring[3]);

        WBPregenExec exec = new WBPregenExec(world, x, z, r, 208, 1000, 1, true);
        exec.addMsgTarget(icommandsender);
        exec.info();
        exec.run();
	}

	@Override
    public int getRequiredPermissionLevel(){ return 3; }	

	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender){
		//if ((sender instanceof EntityPlayerMP) && ((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;
		return super.canCommandSenderUseCommand(sender);
    }
	
	
}
