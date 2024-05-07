package mods.zacharymei.de.mixin;

import mods.zacharymei.de.api.DurationalEnchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Inject(method = "jump", at = @At("TAIL"))
    public void onJump(CallbackInfo ci){


        ItemStack current_armor_chest = this.getEquippedStack(EquipmentSlot.CHEST);
        if(!current_armor_chest.isEmpty()){

            Enchantment enchantment = Enchantments.THORNS;

            DurationalEnchant.create((PlayerEntity) (Object) this, current_armor_chest,
                    DurationalEnchant.Builder(enchantment));


        }




    }

}
