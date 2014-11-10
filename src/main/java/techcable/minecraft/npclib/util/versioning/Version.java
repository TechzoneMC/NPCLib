package techcable.minecraft.npclib.util.versioning;

import lombok.*;

@Getter
@RequiredArgsConstructor
public abstract class Version {
	private final String id;
	public abstract boolean isVersion();
}
