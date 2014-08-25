package mcp.mobius.pregen.exec;

import java.util.HashSet;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public abstract class BaseExec {

	protected boolean                  shouldRun = false;
	protected HashSet<ICommandSender> msgtargets = new HashSet<ICommandSender>();
	protected ICommandSender         firstTarget = null;
	
	public void run(){
		this.shouldRun = true;
	}

    public abstract void exec();

	public void done(){
		this.shouldRun = false; 
		this.msgtargets.clear();
		this.firstTarget = null;
	}
	
	public boolean shouldRun(){
		return this.shouldRun;
	}
	
	public void addMsgTarget(ICommandSender sender){
		if (this.msgtargets.size() == 0) this.firstTarget = sender;
		this.msgtargets.add(sender);
	}
	
	public void sendMsg(String msg){
		for (ICommandSender target : msgtargets)
            target.addChatMessage(new ChatComponentText(msg));
	}
}
