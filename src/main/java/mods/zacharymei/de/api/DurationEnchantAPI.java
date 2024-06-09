package mods.zacharymei.de.api;

import mods.zacharymei.de.impl.Entry;
import mods.zacharymei.de.impl.DEHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DurationEnchantAPI {

    public static int default_duration = 600;

    public static boolean addEnchantment(ItemStack stack, World world, Enchantment enchantment){
        int max_level = enchantment.getMaxLevel();
        int level = world.random.nextInt(max_level)+1;
        return addEnchantment(stack, world, enchantment, level, default_duration, true);
    }

    public static boolean addEnchantment(ItemStack stack, World world, Enchantment enchantment, int level){
        return addEnchantment(stack, world, enchantment, level, default_duration, true);
    }

    public static boolean addEnchantment(ItemStack stack, World world, Enchantment enchantment, int level, int duration){
        return addEnchantment(stack, world, enchantment, level, duration, true);
    }

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

}
