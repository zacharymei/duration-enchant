package mod.zacharymei.de.impl;

import mod.zacharymei.de.DurationEnchant;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DEHandler {

    public static boolean addEnchantment(ItemStack stack, Entry entry, World world){

        if(world.isClient()) return false;

        int exist_level = EnchantmentHelper.getLevel(entry.enchantment(), stack);
        Identifier id = EnchantmentHelper.getEnchantmentId(entry.enchantment());

        if(exist_level == 0){
            DEData.writeNBT(world, stack, entry, exist_level);
            return true;
        }

        if(DEHelper.hasDurationEnchant(stack, id)){
            if(exist_level <= entry.level()){
                DEData.updateNBT(stack, id, world, entry, false);
                return true;
            }
        }

        if(exist_level >= entry.level()){
            DurationEnchant.LOGGER.info("Giving enchantment to item already have one. ");
            return false;
        }

        DEData.updateNBT(stack, id, world, entry, true);
        return true;
    }

    public static void timeout(ItemStack stack, Identifier id){
        NbtCompound nbt = DEData.getNBT(stack, id);
        int exist_level = DEData.getExistLevel(nbt);

        if(exist_level > 0){
            DEData.removeDENBT(stack, id);
            DEHelper.setEnchantmentLevel(stack, id, exist_level);
        }else{
            DEHelper.removeEnchantment(stack, id);
        }
    }




}
