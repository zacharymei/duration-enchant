package mods.zacharymei.de;

import mods.zacharymei.de.event.ItemStackClientEvents;
import mods.zacharymei.de.impl.RenderDETooltip;
import net.fabricmc.api.ClientModInitializer;

public class DurationalEnchantClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ItemStackClientEvents.FINISH_DRAW_ENCHANTMENT.register(new RenderDETooltip());

	}
}