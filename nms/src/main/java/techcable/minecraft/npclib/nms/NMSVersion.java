package techcable.minecraft.npclib.nms;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class NMSVersion {
	private final Class<? extends NMS> nmsClass;
	private final String version;

	private NMS nms;
	public NMS getNMS() {
		if (nms == null) {
			try {
				Constructor<? extends NMS> constructor = getNmsClass().getConstructor();
				constructor.setAccessible(true);
				nms = constructor.newInstance();
			} catch (Exception ex) {
				throw new RuntimeException("Unable to call no arg constructor for " + nmsClass.getName(), ex);
			}
		}
		return nms;
	}
	
	public static String determineCurrentVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf(".") + 1);
	}
	
	public static NMSVersion getVersion(String version) throws UnknownNMSVersionException {
		try {
			Class<?> rawClass = Class.forName("techcable.minecraft.npclib.nms.versions." + version + ".NMS");
			return new NMSVersion(rawClass.asSubclass(NMS.class), version);
		} catch (Exception ex) {
			throw new UnknownNMSVersionException(version);
		}
	}
}