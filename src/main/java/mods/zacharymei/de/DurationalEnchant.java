package mods.zacharymei.de;

import mods.zacharymei.de.event.ItemStackEvents;
import mods.zacharymei.de.impl.DurationEnchantmentUpdater;
import mods.zacharymei.de.networking.DENetworkHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationalEnchant implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Durational Enchant");

	@Override
	public void onInitialize() {

		ServerTickEvents.END_SERVER_TICK.register(new DurationEnchantmentUpdater());
		ItemStackEvents.END_INVENTORY_TICK.register(new DurationEnchantmentUpdater());

		ServerPlayNetworking.registerGlobalReceiver(DENetworkHandler.DE_INSTANCE_UPDATE_PACKET_ID, DENetworkHandler::onUpdateRequest);

	}
}