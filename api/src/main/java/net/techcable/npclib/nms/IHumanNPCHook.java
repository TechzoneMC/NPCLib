package net.techcable.npclib.nms;

import java.util.UUID;

public interface IHumanNPCHook extends ILivingNPCHook {

    public void setSkin(UUID id); //Remember to do this while despawned

    public void showInTablist();

    public void hideFromTablist();
}
