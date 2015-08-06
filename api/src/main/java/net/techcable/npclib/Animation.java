package net.techcable.npclib;

import lombok.*;

/**
 * An animation the npc can perform
 * <p>
 * An animation is a visual-action
 * It may or may not correlate with a real action
 * </p>
 */
@RequiredArgsConstructor
public enum Animation {
    /**
     * Makes the npc act hurt
     * <p/>
     * Only applicable to living entities
     */
    HURT,
    /**
     * Makes the npc lie on the ground
     * <p/>
     * Only applicable to living entities
     */
    DEAD,

    // Human Animations

    /**
     * Makes the npc swing its arm
     * <p/>
     * Only applicable to human entities
     */
    ARM_SWING(HumanNPC.class),
    /**
     * Makes the npc eat the item it is holding
     * <p/>
     * Only applicable to human entities
     */
    EAT(HumanNPC.class),
    /**
     * A critical hit animation
     * <p/>
     * Only applicable to human entities
     */
    CRITICAL(HumanNPC.class),
    /**
     * A 'magic' critical hit animation
     * <p/>
     * Only applicable to human entities
     */
    MAGIC_CRITICAL(HumanNPC.class);


    private final Class<? extends NPC> appliesTo;

    /**
     * Get the type of npc this animation can be applied to
     *
     * @return the type of npc this animation can be applied to
     */
    public Class<? extends NPC> getAppliesTo() {
        return appliesTo;
    }

    private Animation() {
        this(LivingNPC.class);
    }
}
