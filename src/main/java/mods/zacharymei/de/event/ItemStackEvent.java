package mods.zacharymei.de.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class ItemStackEvent {

    private ItemStackEvent(){}

    public static final Event<InventoryTick> END_INVENTORY_TICK = EventFactory.createArrayBacked(InventoryTick.class, (listeners)->(stack, world, entity, slot, selected)->{
        for(InventoryTick listener: listeners){
            listener.endInventoryTick(stack, world, entity, slot, selected);
        }
    });

    @FunctionalInterface
    public interface InventoryTick{
        void endInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected);
    }

}
