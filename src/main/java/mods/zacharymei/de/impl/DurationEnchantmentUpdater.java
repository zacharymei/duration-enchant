package mods.zacharymei.de.impl;

import mods.zacharymei.de.event.ItemStackEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.UUID;

public class DurationEnchantmentUpdater implements ServerTickEvents.EndTick, ItemStackEvents.InventoryTick {

    @Override
    public void onEndTick(MinecraftServer server) {
        DurationEnchantmentRegistry.getState(server).isTimeout(server.getOverworld().getTime());
    }

    @Override
    public void afterInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient() && world.getServer() != null){
            NbtList de_list = stack.getOrCreateNbt().getList(DurationEnchantImpl.KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
            for(NbtElement e: de_list){
                UUID instance_id  = ((NbtCompound) e).getUuid(DurationEnchantImpl.KEY_INSTANCE_ID);
                DurationEnchantmentRegistry ter = DurationEnchantmentRegistry.getState(world.getServer());
                ter.isTimeout((ItemStack) (Object) stack, instance_id);
            }
        }
    }



}
