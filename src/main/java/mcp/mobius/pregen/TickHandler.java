package mcp.mobius.pregen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcp.mobius.pregen.exec.*;

public class TickHandler {

    @SubscribeEvent
    public void foo(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        if(WBPregenExec.instance != null && WBPregenExec.instance.shouldRun())
            WBPregenExec.instance.exec();

        if (PregenExec.instance.shouldRun())
            PregenExec.instance.exec();

        if (PurgeChunks.instance.shouldRun())
            PurgeChunks.instance.exec();

        if (ScanChunkExec.instance.shouldRun())
            ScanChunkExec.instance.exec();
    }

}
