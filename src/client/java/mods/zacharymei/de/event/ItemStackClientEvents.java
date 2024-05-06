package mods.zacharymei.de.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ItemStackClientEvents {

    private ItemStackClientEvents(){}

    public static final Event<AppendEnchantmentTooltip> FINISH_DRAW_ENCHANTMENT = EventFactory.createArrayBacked(AppendEnchantmentTooltip.class, (listeners)->(list, player, stack, context)->{
        for(AppendEnchantmentTooltip listener: listeners){
            listener.append(list, player, stack, context);
        }
    });

    @FunctionalInterface
    public interface AppendEnchantmentTooltip{
        void append(List<Text> list, @Nullable PlayerEntity player, ItemStack stack, TooltipContext context);
    }

}
