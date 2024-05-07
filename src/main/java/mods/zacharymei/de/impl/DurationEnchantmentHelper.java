package mods.zacharymei.de.impl;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Map;

public class DurationEnchantmentHelper {

    static void removeExistEnchantment(ItemStack stack, Enchantment enchantment){

        NbtList list = stack.getOrCreateNbt().getList(ItemStack.ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(list);
        stack.removeSubNbt(ItemStack.ENCHANTMENTS_KEY);

        for(Map.Entry<Enchantment, Integer> entry: enchantments.entrySet()){
            if(entry.getKey() != enchantment) stack.addEnchantment(entry.getKey(), entry.getValue());
        }

    }

    public static boolean hasDurationEnchantment(ItemStack stack, Enchantment enchantment){
        return DurationEnchantImpl.hasDurationEnchantment(stack.getOrCreateNbt(), enchantment) != null;
    }







}
