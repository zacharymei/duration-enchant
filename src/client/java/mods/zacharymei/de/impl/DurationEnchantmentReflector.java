package mods.zacharymei.de.impl;


import com.google.common.collect.Maps;
import mods.zacharymei.de.networking.DEClientNetworkHandler;
import mods.zacharymei.de.networking.DENetworkHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DurationEnchantmentReflector implements ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Disconnect {

    public static Map<UUID, DurationEnchantmentInstance> LOCAL_CONCURRENT_DURATION_ENCHANTMENTS = Maps.newConcurrentMap();

    static void requireUpdate(UUID instance_id){
        DEClientNetworkHandler.requireUpdate(instance_id);
    }

    public static void retrieveUpdate(DurationEnchantmentInstance instance, Long world_time){
        instance.setTimeout((int) (instance.getCreated_time() + instance.getDuration() - world_time));
        LOCAL_CONCURRENT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);
    }

    static void updateTimeout(){
        for(Map.Entry<UUID, DurationEnchantmentInstance> entry: LOCAL_CONCURRENT_DURATION_ENCHANTMENTS.entrySet()){
            entry.getValue().update(()->{
                LOCAL_CONCURRENT_DURATION_ENCHANTMENTS.remove(entry.getKey());
            });
        }
    }

    @Nullable
    public static DurationEnchantmentInstance getInstance(UUID instance_id){
        if(!LOCAL_CONCURRENT_DURATION_ENCHANTMENTS.containsKey(instance_id)){
            requireUpdate(instance_id);
        }
        return LOCAL_CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id);
    }


    @Override
    public void onEndTick(ClientWorld world) {
        updateTimeout();
    }

    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        LOCAL_CONCURRENT_DURATION_ENCHANTMENTS = Maps.newConcurrentMap();
    }
}
