package mods.zacharymei.de.impl;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DurationEnchantmentRegistry extends PersistentState {

    public static final String KEY_CONCURRENT_LIST = "de.concurrent";
    public static final String KEY_TIMEOUT_LIST = "de.timeout";

    protected static Map<UUID, DurationEnchantmentInstance> CONCURRENT_DURATION_ENCHANTMENTS = Maps.newConcurrentMap();
    protected static Map<UUID, DurationEnchantmentInstance> TIMEOUT_DURATION_ENCHANTMENTS = Maps.newHashMap();


    public void isTimeout(ItemStack stack, UUID instance_id){
        if(CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id) != null) return;
        timeout(stack, instance_id);
    }

    void timeout(ItemStack stack, UUID instance_id){
        DurationEnchant.timeoutDurationEnchantment(stack, TIMEOUT_DURATION_ENCHANTMENTS.get(instance_id));
        TIMEOUT_DURATION_ENCHANTMENTS.remove(instance_id);
    }

    void earlyTimeout(ItemStack stack, UUID instance_id){
        DurationEnchant.timeoutDurationEnchantment(stack, CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id));
        CONCURRENT_DURATION_ENCHANTMENTS.remove(instance_id);
    }

    public void update(World world){
        for(DurationEnchantmentInstance instance: CONCURRENT_DURATION_ENCHANTMENTS.values()){
            instance.update(()->{
                CONCURRENT_DURATION_ENCHANTMENTS.remove(instance.getInstance_id());
                TIMEOUT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);

            });

        }
    }

    Identifier register(PlayerEntity player, ItemStack stack, DurationEnchantmentInstance de_instance){

        Identifier key = generateKey(player, stack, de_instance);
        CONCURRENT_DURATION_ENCHANTMENTS.put(key.getInstance_id(), de_instance);

        return key;
    }

    static void removeConcurrentInstance(UUID instance_id){
        CONCURRENT_DURATION_ENCHANTMENTS.remove(instance_id);
    }

    @Nullable
    public static DurationEnchantmentInstance getInstance(UUID instance_id){
        DurationEnchantmentInstance instance = CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id);
        if(instance == null) instance = TIMEOUT_DURATION_ENCHANTMENTS.get(instance_id);
        return instance;
    }

    public static List<DurationEnchantmentInstance> getItemDurationEnchantmentInstances(ItemStack stack){
        List<DurationEnchantmentInstance> list = new ArrayList<>();
        NbtList de_list = stack.getOrCreateNbt().getList(DurationEnchant.KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
        for(NbtElement e: de_list){
            DurationEnchantmentInstance instance = getInstance(((NbtCompound) e).getUuid(DurationEnchant.KEY_INSTANCE_ID));
            if(instance != null) list.add(instance);
        }
        return list;
    }




    private static Identifier generateKey(PlayerEntity player, ItemStack stack, DurationEnchantmentInstance de_instance){
        return new Identifier(
                player.getUuid(),
                Registries.ITEM.getId(stack.getItem()),
                player.getWorld().getTime(),
                Registries.ENCHANTMENT.getId(de_instance.getEnchantment()),
                de_instance.getInstance_id()
        );
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound de_nbt = new NbtCompound();
        NbtList list = new NbtList();
        for(DurationEnchantmentInstance instance: CONCURRENT_DURATION_ENCHANTMENTS.values()){
            list.add(instance.createNBT());
        }
        NbtList timeout_list = new NbtList();
        for(DurationEnchantmentInstance instance: TIMEOUT_DURATION_ENCHANTMENTS.values()){
            timeout_list.add(instance.createNBT());
        }

        de_nbt.put(KEY_CONCURRENT_LIST, list);
        de_nbt.put(KEY_TIMEOUT_LIST, timeout_list);

        nbt.put(DurationEnchant.KEY_DURATION_ENCHANTMENTS, de_nbt);

        return nbt;
    }

    public static DurationEnchantmentRegistry createFromNbt(NbtCompound nbt) {

        DurationEnchantmentRegistry state = new DurationEnchantmentRegistry();

        NbtList list = nbt.getCompound(DurationEnchant.KEY_DURATION_ENCHANTMENTS).getList(KEY_CONCURRENT_LIST, NbtElement.COMPOUND_TYPE);
        NbtList timeout_list = nbt.getCompound(DurationEnchant.KEY_DURATION_ENCHANTMENTS).getList(KEY_TIMEOUT_LIST, NbtElement.COMPOUND_TYPE);
        if(CONCURRENT_DURATION_ENCHANTMENTS.isEmpty()){
            for(NbtElement instance_nbt: list){
                DurationEnchantmentInstance instance = DurationEnchantmentInstance.fromNBT((NbtCompound) instance_nbt);
                if(instance != null) CONCURRENT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);
            }
        }
        if(TIMEOUT_DURATION_ENCHANTMENTS.isEmpty()){
            for(NbtElement instance_nbt: timeout_list){
                DurationEnchantmentInstance instance = DurationEnchantmentInstance.fromNBT((NbtCompound) instance_nbt);
                if(instance != null) TIMEOUT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);
            }
        }

        return state;
    }

    private static Type<DurationEnchantmentRegistry> type = new Type<>(DurationEnchantmentRegistry::new, DurationEnchantmentRegistry::createFromNbt, null);

    public static DurationEnchantmentRegistry getState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        DurationEnchantmentRegistry state = persistentStateManager.getOrCreate(type, DurationEnchant.KEY_DURATION_ENCHANTMENTS);
        state.markDirty();
        return state;
    }





    static class Identifier{

        private final UUID player_created;
        private final net.minecraft.util.Identifier stack;
        private final Long time_created;
        private final net.minecraft.util.Identifier enchantment;
        private final UUID instance_id;

        private Identifier(UUID player, net.minecraft.util.Identifier stack, Long time_created, net.minecraft.util.Identifier enchantment, UUID instance_id){
            this.player_created = player;
            this.stack = stack;
            this.time_created = time_created;
            this.enchantment = enchantment;
            this.instance_id = instance_id;
        }


        public UUID getPlayer_created() {
            return player_created;
        }

        public net.minecraft.util.Identifier getStack() {
            return stack;
        }

        public Long getTime_created() {
            return time_created;
        }

        public net.minecraft.util.Identifier getEnchantment() {
            return enchantment;
        }

        public UUID getInstance_id() {
            return instance_id;
        }
    }






}
