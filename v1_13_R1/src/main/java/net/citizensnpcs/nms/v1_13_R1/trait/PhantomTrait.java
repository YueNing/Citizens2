package net.citizensnpcs.nms.v1_13_R1.trait;

import org.bukkit.entity.Phantom;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("phantomtrait")
public class PhantomTrait extends Trait {
    @Persist
    private int size = 1;

    public PhantomTrait() {
        super("phantomtrait");
    }

    @Override
    public void run() {
        if (npc.isSpawned() && npc.getEntity() instanceof Phantom) {
            Phantom phantom = (Phantom) npc.getEntity();
            phantom.setSize(size);
        }
    }

    public void setSize(int size) {
        this.size = size;
    }
}
