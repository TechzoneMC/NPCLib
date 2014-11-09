package techcable.minecraft.npclib.nms.v1_8ProtocolHack;

public class NMS1_8ProtocolHack extends NMS1_7_10 {
    @Override    
    public boolean isCompatable(Version version) {
        if (version.getId() == "spigot-1.8-protocolhack") return true;
        return false;
    }
}