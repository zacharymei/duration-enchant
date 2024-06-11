package mod.zacharymei.de.impl;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import static net.minecraft.item.ItemStack.ENCHANTMENTS_KEY;

public class DEHelper {

    public static void removeEnchantment(ItemStack stack, Identifier id){
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList list = nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=list.size()-1;i>=0;i--){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))) list.remove(i);
        }
        if(list.isEmpty()) nbt.remove(ENCHANTMENTS_KEY);
    }

    public static void setEnchantmentLevel(ItemStack stack, Identifier id, int level){
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList list = nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=list.size()-1;i>=0;i--){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))){
                EnchantmentHelper.writeLevelToNbt(content, level);
            }
        }
    }

    public static boolean hasDurationEnchant(ItemStack stack, Identifier id){
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList list = nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=list.size()-1;i>=0;i--){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))){
                return DEData.isDurationEnchant(content);
            }
        }
        return false;
    }

}
