package mod.zacharymei.de.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import mod.zacharymei.de.api.DurationEnchantAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.*;

public class DECommand implements ModInitializer {

    private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.stringifiedTranslatable("commands.enchant.failed.itemless", entityName));
    private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(itemName -> Text.stringifiedTranslatable("commands.enchant.failed.incompatible", itemName));
    private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((level, maxLevel) -> Text.stringifiedTranslatable("commands.enchant.failed.level", level, maxLevel));


    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("duration").requires(source -> source.hasPermissionLevel(2))
                    .then(argument("target", EntityArgumentType.player())
                            .then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                    .executes((ctx)->execute(ctx, -1, -1))
                                    .then(argument("level", IntegerArgumentType.integer(1))
                                            .executes((ctx)->execute(ctx, IntegerArgumentType.getInteger(ctx, "level"), -1))
                                            .then(argument("duration in second", IntegerArgumentType.integer(1))
                                                    .executes((ctx)->execute(ctx, IntegerArgumentType.getInteger(ctx, "level"), IntegerArgumentType.getInteger(ctx, "duration in second")))))
            )));
        });
    }

    static int execute(CommandContext<ServerCommandSource> context, int level, int duration_sec) throws CommandSyntaxException {

        PlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        Enchantment enchantment = RegistryEntryArgumentType.getEnchantment(context, "enchantment").value();
        ItemStack stack = player.getMainHandStack();

        if(stack.isEmpty()) throw FAILED_ITEMLESS_EXCEPTION.create(player.getName().getString());
        if(level > enchantment.getMaxLevel()) throw FAILED_LEVEL_EXCEPTION.create(level, enchantment.getMaxLevel());
        if (!enchantment.isAcceptableItem(stack)) throw FAILED_INCOMPATIBLE_EXCEPTION.create(stack.getItem().getName(stack).getString());

        int duration = (int) (duration_sec * player.getWorld().getTickManager().getTickRate());
        boolean result = DurationEnchantAPI.addEnchantment(stack, player.getWorld(), enchantment, level, duration, true);

        if(result){
            context.getSource().sendFeedback(()->Text.translatable("commands.enchant.success.single", enchantment.getName(level), player.getDisplayName()).append(" for "+duration_sec+" seconds. "), true);
        }
        else{
            context.getSource().sendFeedback(()->Text.literal("Giving enchantment to item already have one. ").formatted(Formatting.RED), false);
        }

        return 1;
    }




}
