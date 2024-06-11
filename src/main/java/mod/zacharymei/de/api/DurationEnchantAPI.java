package mod.zacharymei.de.api;

import mod.zacharymei.de.impl.DEData;
import mod.zacharymei.de.impl.Entry;
import mod.zacharymei.de.impl.DEHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DurationEnchantAPI {

    public static int default_duration = 600;

    public static boolean addEnchantment(ItemStack stack, World world, Enchantment enchantment, int level, int duration, boolean showTime){
        if(stack.isEmpty()) return false;
        if(world == null || world.isClient()) return false;

        int max_level = enchantment.getMaxLevel();
        level = (level < 1)? world.random.nextInt(max_level)+1: level;
        level = Math.min(level, max_level);

        duration = (duration < 1)? default_duration: duration;

        Entry entry = new Entry(enchantment, level, duration, showTime);
        return DEHandler.addEnchantment(stack, entry, world);

    }

    public static int getRemainTick(NbtCompound nbt, World world){
        if(world == null) return 0;
        return (int) (DEData.getTimeout(nbt) - world.getTime());
    }

    public static int getRemainTick(ItemStack stack, Enchantment enchantment, World world){
        if(stack == null || stack.isEmpty() || enchantment == null || world == null) return -1;
        Identifier id = EnchantmentHelper.getEnchantmentId(enchantment);
        NbtCompound nbt = DEData.getNBT(stack, id);
        return (DEData.isDurationEnchant(nbt))? getRemainTick(nbt, world) : -1;
    }

    public static long getEnchantmentTimeout(NbtCompound nbt){
        return DEData.getTimeout(nbt);
    }

    public static long getEnchantmentTimeout(ItemStack stack, Enchantment enchantment){
        if(stack == null || stack.isEmpty() || enchantment == null) return -1;
        Identifier id = EnchantmentHelper.getEnchantmentId(enchantment);
        NbtCompound nbt = DEData.getNBT(stack, id);
        return (DEData.isDurationEnchant(nbt))? getEnchantmentTimeout(nbt) : -1;
    }

}
