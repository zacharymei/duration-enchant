package mods.zacharymei.api;

import mods.zacharymei.impl.DurationEnchant;
import mods.zacharymei.impl.DurationEnchantmentInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.UUID;

public class DurationalEnchant {
    public static void create(PlayerEntity player, ItemStack stack, DurationEnchant.Builder builder){
        DurationEnchant.create(player, stack, builder);
    }
}
