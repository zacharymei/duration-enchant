package mods.zacharymei.de.api;

import mods.zacharymei.de.impl.DurationEnchant;
import mods.zacharymei.de.impl.DurationEnchantmentInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.UUID;

public class DurationalEnchant {
    public static void create(PlayerEntity player, ItemStack stack, DurationEnchant.Builder builder){
        if(player.getServer() == null) return;
        DurationEnchant.create(player, stack, builder);
    }

    public static DurationEnchant.Builder Builder(Enchantment enchantment){
        return DurationEnchant.Builder(enchantment);
    }
}
