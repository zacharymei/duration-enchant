package mods.zacharymei.de.impl;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static net.minecraft.item.ItemStack.ENCHANTMENTS_KEY;

public class DEData {

    public static final String KEY_DURATION = "duration";
    public static final String KEY_CREATED_TIME = "created_time";
    public static final String KEY_EXIST_LEVEL = "exist_lvl";
    public static final String KEY_SHOW_TIME = "show_time";



    public static void writeNBT(World world, ItemStack stack, Entry entry, int exist_level){

        NbtCompound stack_nbt = stack.getOrCreateNbt();
        if (!stack_nbt.contains(ENCHANTMENTS_KEY, NbtElement.LIST_TYPE)) {
            stack_nbt.put(ENCHANTMENTS_KEY, new NbtList());
        }
        NbtList nbtList = stack_nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);

        Identifier id = EnchantmentHelper.getEnchantmentId(entry.enchantment());
        NbtCompound nbt = EnchantmentHelper.createNbt(id, (byte) entry.level());

        nbt.putInt(KEY_DURATION, entry.duration());
        nbt.putLong(KEY_CREATED_TIME, world.getTime());
        nbt.putInt(KEY_EXIST_LEVEL, exist_level);
        nbt.putBoolean(KEY_SHOW_TIME, entry.showTime());
        nbtList.add(nbt);
    }


    public static void removeDENBT(ItemStack stack, Identifier id){
        NbtList list = stack.getOrCreateNbt().getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=list.size()-1;i>=0;i--){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))){
                content.remove(KEY_DURATION);
                content.remove(KEY_CREATED_TIME);
                content.remove(KEY_EXIST_LEVEL);
                content.remove(KEY_SHOW_TIME);
                return;
            }
        }
    }

    public static void updateNBT(ItemStack stack, Identifier id, World world, Entry entry, boolean write_exist){
        NbtList list = stack.getOrCreateNbt().getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=list.size()-1;i>=0;i--){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))){
                if(write_exist){
                    int exist_level = EnchantmentHelper.getLevelFromNbt(content);
                    content.putInt(KEY_EXIST_LEVEL, exist_level);
                }
                EnchantmentHelper.writeLevelToNbt(content, entry.level());
                content.putInt(KEY_DURATION, entry.duration());
                content.putLong(KEY_CREATED_TIME, world.getTime());
                content.putBoolean(KEY_SHOW_TIME, entry.showTime());
                return;
            }
        }
    }

    public static boolean isDurationEnchant(NbtCompound nbt){
        return nbt.contains(KEY_CREATED_TIME);
    }


    public static int getExistLevel(ItemStack stack, Identifier id){
        NbtList list = stack.getOrCreateNbt().getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        for(int i=0;i<list.size();++i){
            NbtCompound content = (NbtCompound) list.get(i);
            if(id.equals(EnchantmentHelper.getIdFromNbt(content))){
                return content.getInt(KEY_EXIST_LEVEL);
            }
        }
        return 0;
    }

    public static long getTimeout(NbtCompound nbt){
        long created_time = nbt.getLong(KEY_CREATED_TIME);
        int duration = nbt.getInt(KEY_DURATION);
        return created_time + duration;
    }

    public static boolean shouldShowTime(NbtCompound nbt){
        return !nbt.contains(KEY_SHOW_TIME) || nbt.getBoolean(KEY_SHOW_TIME);
    }





}
