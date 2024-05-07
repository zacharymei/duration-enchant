package mods.zacharymei.de.impl;

import mods.zacharymei.de.api.DurationalEnchant;
import mods.zacharymei.de.event.ItemStackClientEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class RenderDETooltip implements ItemStackClientEvents.AppendEnchantmentTooltip {

    static final int render_delay = 10;


    @Override
    public void append(List<Text> list, @Nullable PlayerEntity player, ItemStack stack, TooltipContext context) {

        List<DurationEnchantmentRegistry.Identifier> de_info = DurationalEnchant.getItemDurationEnchantmentInfo(stack);

        de_info.forEach(info->{
            DurationEnchantmentInstance de_instance = DurationEnchantmentReflector.getInstance(info.getInstance_id());
            if(de_instance != null){
                for(Text t:list){
                    if(t.contains(de_instance.getEnchantment().getName(de_instance.getLevel()))) drawEnchantmentText(t, de_instance);
                }
            }

        });
    }

    static void drawEnchantmentText(Text t, DurationEnchantmentInstance de){

        ((MutableText) t).append(ScreenTexts.SPACE).append(Text.literal(convertTickToTime(de.getTimeout())));
        ((MutableText) t).formatted(Formatting.GOLD);
    }

    static String convertTickToTime(int duration){
        duration += render_delay;
        int total_sec = duration/20;
        int minute = total_sec/60;
        int sec = total_sec%60;
        String minuteStr = (minute < 10)? "0"+minute: String.valueOf(minute);
        return minuteStr +":"+ (new DecimalFormat("00").format(sec));
    }




}
