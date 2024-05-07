package mods.zacharymei.de.api;

import mods.zacharymei.de.impl.DurationEnchantImpl;
import mods.zacharymei.de.impl.DurationEnchantmentRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DurationalEnchant {

    public static void create(PlayerEntity player, ItemStack stack, DurationEnchantImpl.Builder builder){
        if(player.getWorld().isClient() || player.getServer() == null) return;
        DurationEnchantImpl.create(player, stack, builder);
    }

    public static DurationEnchantImpl.Builder Builder(Enchantment enchantment){
        return DurationEnchantImpl.Builder(enchantment);
    }

    public static List<DurationEnchantmentRegistry.Identifier> getItemDurationEnchantmentInfo(ItemStack stack){
        return DurationEnchantImpl.getItemDurationEnchantmentInfo(stack);
    }


}
