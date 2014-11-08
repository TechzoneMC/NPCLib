package techcable.minecraft.npclib.util.versioning;

@Getter
@RequiredArgsConstructor
public abstract class Version {
	private final String id;
	public abstract boolean isVersion();
}
