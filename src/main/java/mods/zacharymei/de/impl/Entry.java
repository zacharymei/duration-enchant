package mods.zacharymei.de.impl;

import net.minecraft.enchantment.Enchantment;

public record Entry(Enchantment enchantment, int level, int duration, boolean showTime) {
}
