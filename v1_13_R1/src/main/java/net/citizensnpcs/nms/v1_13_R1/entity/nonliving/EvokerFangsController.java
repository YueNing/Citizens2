package net.citizensnpcs.nms.v1_13_R1.entity.nonliving;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEvokerFangs;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.nms.v1_13_R1.entity.MobEntityController;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_13_R1.EntityEvokerFangs;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EnumHand;
import net.minecraft.server.v1_13_R1.EnumInteractionResult;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.Vec3D;
import net.minecraft.server.v1_13_R1.World;

public class EvokerFangsController extends MobEntityController {
    public EvokerFangsController() {
        super(EntityEvokerFangsNPC.class);
    }

    @Override
    public EvokerFangs getBukkitEntity() {
        return (EvokerFangs) super.getBukkitEntity();
    }

    public static class EntityEvokerFangsNPC extends EntityEvokerFangs implements NPCHolder {
        private final CitizensNPC npc;

        public EntityEvokerFangsNPC(World world) {
            this(world, null);
        }

        public EntityEvokerFangsNPC(World world, NPC npc) {
            super(world);
            this.npc = (CitizensNPC) npc;
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            if (npc == null) {
                return super.a(entityhuman, vec3d, enumhand);
            }
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent((Player) entityhuman.getBukkitEntity(),
                    getBukkitEntity());
            Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled() ? EnumInteractionResult.FAIL : EnumInteractionResult.SUCCESS;
        }

        @Override
        public void tick() {
            super.tick();
            if (npc != null) {
                npc.update();
            }
        }

        @Override
        public void collide(net.minecraft.server.v1_13_R1.Entity entity) {
            // this method is called by both the entities involved - cancelling
            // it will not stop the NPC from moving.
            super.collide(entity);
            if (npc != null) {
                Util.callCollisionEvent(npc, entity.getBukkitEntity());
            }
        }

        @Override
        public boolean d(NBTTagCompound save) {
            return npc == null ? super.d(save) : false;
        }

        @Override
        public void f(double x, double y, double z) {
            if (npc == null) {
                super.f(x, y, z);
                return;
            }
            if (NPCPushEvent.getHandlerList().getRegisteredListeners().length == 0) {
                if (!npc.data().get(NPC.DEFAULT_PROTECTED_METADATA, true))
                    super.f(x, y, z);
                return;
            }
            Vector vector = new Vector(x, y, z);
            NPCPushEvent event = Util.callPushEvent(npc, vector);
            if (!event.isCancelled()) {
                vector = event.getCollisionVector();
                super.f(vector.getX(), vector.getY(), vector.getZ());
            }
            // when another entity collides, this method is called to push the
            // NPC so we prevent it from doing anything if the event is
            // cancelled.
        }

        @Override
        public CraftEntity getBukkitEntity() {
            if (npc != null && !(bukkitEntity instanceof NPCHolder)) {
                bukkitEntity = new EvokerFangsNPC(this);
            }
            return super.getBukkitEntity();
        }

        @Override
        public NPC getNPC() {
            return npc;
        }
    }

    public static class EvokerFangsNPC extends CraftEvokerFangs implements NPCHolder {
        private final CitizensNPC npc;

        public EvokerFangsNPC(EntityEvokerFangsNPC entity) {
            super((CraftServer) Bukkit.getServer(), entity);
            this.npc = entity.npc;
        }

        @Override
        public NPC getNPC() {
            return npc;
        }
    }
}