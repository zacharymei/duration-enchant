package mods.zacharymei.de;

import mods.zacharymei.de.event.ItemStackEvent;
import mods.zacharymei.de.impl.DETracker;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationEnchant implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Duration Enchant");
	public static final String MOD_ID = "duration-enchant";

	@Override
	public void onInitialize() {
		ItemStackEvent.END_INVENTORY_TICK.register(new DETracker());
	}
}