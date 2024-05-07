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
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("duration-enchant");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ServerTickEvents.END_SERVER_TICK.register(new DurationEnchantmentUpdater());
		ItemStackEvents.END_INVENTORY_TICK.register(new DurationEnchantmentUpdater());

		ServerPlayNetworking.registerGlobalReceiver(DENetworkHandler.DE_INSTANCE_UPDATE_PACKET_ID, DENetworkHandler::onUpdateRequest);

	}
}