package techcable.minecraft.npclib.util.versioning;

@Getter
public class CraftbukkitVersion implements Version {
    private final String minecraftVersion;
    private final String packageVersion;
    public CraftbukkitVersion(String id, String minecraftVersion, String packageVersion) {
	    super(id);
	    this.minecraftVersion = minecraftVersion;
	    this.packageVersion = packageVersion;
    }

    @Override
    public boolean isVersion() {
	    try {
		    Class.forName("net.minecraft.server.v" + getPackageVersion());
	    } catch (Exception ex) {
		    return false;
	    }
	    return true;
    }
}
