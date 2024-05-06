package mods.zacharymei.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class ItemStackEvents {

    private ItemStackEvents(){}

    public static final Event<InventoryTick> END_INVENTORY_TICK = EventFactory.createArrayBacked(InventoryTick.class, (listeners)->(stack, world, entity, slot, selected)->{
        for(InventoryTick listener: listeners){
            listener.afterInventoryTick(stack, world, entity, slot, selected);
        }
    });

    @FunctionalInterface
    public interface InventoryTick{
        void afterInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected);
    }

}
