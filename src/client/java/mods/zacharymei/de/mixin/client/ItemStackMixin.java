package mods.zacharymei.de.mixin.client;

import mods.zacharymei.de.impl.DEData;
import mods.zacharymei.de.DETooltip;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V"))
    public void getTooltip(List<Text> tooltip, NbtList enchantments){
        for (int i = 0; i < enchantments.size(); ++i) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e->{
                int level = EnchantmentHelper.getLevelFromNbt(nbtCompound);
                long timeout = DEData.getTimeout(nbtCompound);
                boolean showTime = DEData.shouldShowTime(nbtCompound);
                Text text = (DEData.isDurationEnchant(nbtCompound))? DETooltip.getText(e, level, timeout, showTime): e.getName(level);
                if(text != null) tooltip.add(text);
            });

        }
    }


}
