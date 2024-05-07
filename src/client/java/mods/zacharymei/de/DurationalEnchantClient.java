package mods.zacharymei.de;

import mods.zacharymei.de.event.ItemStackClientEvents;
import mods.zacharymei.de.impl.DurationEnchantmentReflector;
import mods.zacharymei.de.impl.RenderDETooltip;
import mods.zacharymei.de.networking.DEClientNetworkHandler;
import mods.zacharymei.de.networking.DENetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DurationalEnchantClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ItemStackClientEvents.FINISH_DRAW_ENCHANTMENT.register(new RenderDETooltip());

		ClientTickEvents.END_WORLD_TICK.register(new DurationEnchantmentReflector());
		ClientPlayConnectionEvents.DISCONNECT.register(new DurationEnchantmentReflector());

		ClientPlayNetworking.registerGlobalReceiver(DENetworkHandler.DE_INSTANCE_UPDATE_PACKET_ID, DEClientNetworkHandler::onGetInstance);

	}
}