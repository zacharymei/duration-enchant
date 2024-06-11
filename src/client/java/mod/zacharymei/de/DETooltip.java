package mod.zacharymei.de;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class DETooltip {

    public static int text_color = 0xFFAA00;
    private static final int render_delay = 10;

    @Nullable
    public static Text getText(Enchantment enchantment, int level, long timeout, boolean showTime){

        ClientWorld world = MinecraftClient.getInstance().world;
        MutableText name = ((MutableText) enchantment.getName(level)).withColor(text_color);

        if(world == null) return name;
        long world_time = world.getTime();

        int remain_tick = (int) (timeout - world_time);
        if(remain_tick < 0) return null;

        if(showTime) name.append(ScreenTexts.SPACE).append(tickToTime(remain_tick)).withColor(text_color);

        return name;
    }

    static String tickToTime(int duration){
        duration += render_delay;
        int total_sec = duration/20;
        int minute = total_sec/60;
        int sec = total_sec%60;
        String minuteStr = (minute < 10)? "0"+minute: String.valueOf(minute);
        return minuteStr +":"+ (new DecimalFormat("00").format(sec));
    }




}
