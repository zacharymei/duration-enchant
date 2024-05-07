package mods.zacharymei.de.networking;

import mods.zacharymei.de.impl.DurationEnchantmentInstance;
import mods.zacharymei.de.impl.DurationEnchantmentRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class DENetworkHandler {

    public static final Identifier DE_INSTANCE_UPDATE_PACKET_ID = new Identifier("de", "instance_packet");

    public static void sendUpdate(ServerPlayerEntity player, DurationEnchantmentInstance instance){
        if(player == null || instance == null) return;
        PacketByteBuf buf = PacketByteBufs.create().writeNbt(instance.createNBT());
        if(validate(instance)){
            ServerPlayNetworking.send(player, DE_INSTANCE_UPDATE_PACKET_ID, buf);
        }
    }

    public static void onUpdateRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        UUID instance_id = buf.readUuid();
        if(instance_id == null) return;
        DurationEnchantmentInstance instance = DurationEnchantmentRegistry.getConcurrentInstance(instance_id);
        sendUpdate(player, instance);
    }

    static boolean validate(DurationEnchantmentInstance instance){
        return true;
    }


}
