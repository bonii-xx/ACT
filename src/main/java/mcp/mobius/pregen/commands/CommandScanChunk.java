package mcp.mobius.pregen.commands;

import mcp.mobius.pregen.exec.ScanChunkExec;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandScanChunk extends CommandBase {

    @Override
    public String getCommandName() {
        return "scanchunk";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/scanchunk : Scans the chunk the player is currently standing in for ores (oredicted).";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        if(p_71515_1_ == null)
            return;

        ScanChunkExec.instance.addMsgTarget(p_71515_1_);
        ScanChunkExec.instance.coordinates = p_71515_1_.getPlayerCoordinates();
        ScanChunkExec.instance.world = p_71515_1_.getEntityWorld();
        ScanChunkExec.instance.run();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }
}
