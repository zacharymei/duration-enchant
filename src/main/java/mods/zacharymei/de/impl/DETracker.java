package mods.zacharymei.de.impl;

import mods.zacharymei.de.event.ItemStackEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static net.minecraft.item.ItemStack.ENCHANTMENTS_KEY;

public class DETracker implements ItemStackEvent.InventoryTick {

    @Override
    public void endInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient()){
            long world_time = world.getTime();
            checkTimeout(stack, world_time);
        }
    }

    private void checkTimeout(ItemStack stack, long world_time){
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList list = nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE).copy();

        for(NbtElement e: list){
            NbtCompound content = (NbtCompound) e;
            if(DEData.isDurationEnchant(content)){
                if(DEData.getTimeout(content) <= world_time){
                    Identifier id = EnchantmentHelper.getIdFromNbt(content);
                    DEHandler.timeout(stack, id);
                }
            }
        }

    }

}
