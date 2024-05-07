package mods.zacharymei.de.networking;


import mods.zacharymei.de.impl.DurationEnchantmentInstance;
import mods.zacharymei.de.impl.DurationEnchantmentReflector;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class DEClientNetworkHandler {

    private static final Identifier DE_UPDATE_PACKET_ID = new Identifier("de", "instance_update");

    public static void requireUpdate(UUID instance_id){
        ClientPlayNetworking.send(DENetworkHandler.DE_INSTANCE_UPDATE_PACKET_ID, PacketByteBufs.create().writeUuid(instance_id));
    }

    public static void onGetInstance(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        client.execute(()->{
            NbtCompound nbt = buf.readNbt();
            if(nbt == null) return;
            DurationEnchantmentInstance instance = DurationEnchantmentInstance.fromNBT(nbt);
            if(instance != null) DurationEnchantmentReflector.retrieveUpdate(instance, handler.getWorld().getTime());
        });
    }

}
