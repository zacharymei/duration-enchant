package mods.zacharymei.de.mixin;

import mods.zacharymei.de.event.ItemStackEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean hasNbt();

    @Shadow private @Nullable NbtCompound nbt;

    @Shadow @Final public static String ENCHANTMENTS_KEY;

    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo info){

        ItemStackEvents.END_INVENTORY_TICK.invoker().afterInventoryTick((ItemStack) (Object) this, world, entity, slot, selected);



    }

}
