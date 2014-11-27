package techcable.minecraft.npclib.nms;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class UnknownNMSVersionException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String nmsVersion;
	

}
