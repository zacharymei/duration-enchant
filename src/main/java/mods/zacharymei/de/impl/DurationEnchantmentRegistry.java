package mods.zacharymei.de.impl;

import com.google.common.collect.Maps;
import mods.zacharymei.de.networking.DENetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DurationEnchantmentRegistry extends PersistentState {

    public static final String KEY_CONCURRENT_LIST = "de.concurrent";
    public static final String KEY_TIMEOUT_LIST = "de.timeout";

    private static final int maxTimeoutMapSize = 30;

    protected static Map<UUID, DurationEnchantmentInstance> CONCURRENT_DURATION_ENCHANTMENTS = Maps.newConcurrentMap();
    protected static LinkedHashMap<UUID, DurationEnchantmentInstance> TIMEOUT_DURATION_ENCHANTMENTS = new LinkedHashMap<>(){
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, DurationEnchantmentInstance> eldest){
            return size() > maxTimeoutMapSize;
        }

    };


    public void isTimeout(ItemStack stack, UUID instance_id){
        if(CONCURRENT_DURATION_ENCHANTMENTS.containsKey(instance_id)) return;
        DurationEnchantImpl.timeoutItemInfo(stack, TIMEOUT_DURATION_ENCHANTMENTS.get(instance_id));
    }

    public void isTimeout(Long world_time){
        for(DurationEnchantmentInstance instance: CONCURRENT_DURATION_ENCHANTMENTS.values()){
            if(instance.hasReachTime(world_time)){
                timeout(instance);
            }
        }
    }

    void timeout(DurationEnchantmentInstance instance){
        TIMEOUT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);
        CONCURRENT_DURATION_ENCHANTMENTS.remove(instance.getInstance_id());
        instance.setTimeout(0);
    }

    void forceTimeout(ItemStack stack, UUID instance_id){
        DurationEnchantmentInstance instance = CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id);
        DurationEnchantImpl.timeoutItemInfo(stack, instance);
        if(instance != null){
            timeout(instance);
        }

    }



    Identifier register(PlayerEntity player, ItemStack stack, DurationEnchantmentInstance de_instance){

        Identifier key = generateKey(player, stack, de_instance);
        CONCURRENT_DURATION_ENCHANTMENTS.put(key.getInstance_id(), de_instance);
        DENetworkHandler.sendUpdate((ServerPlayerEntity) player, de_instance);
        return key;
    }

    @Nullable
    public static DurationEnchantmentInstance getInstance(UUID instance_id){
        DurationEnchantmentInstance instance = CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id);
        if(instance == null) instance = TIMEOUT_DURATION_ENCHANTMENTS.get(instance_id);
        return instance;
    }

    public static DurationEnchantmentInstance getConcurrentInstance(UUID instance_id){
        return CONCURRENT_DURATION_ENCHANTMENTS.get(instance_id);
    }


    private static Identifier generateKey(PlayerEntity player, ItemStack stack, DurationEnchantmentInstance de_instance){
        return new Identifier(
                player.getUuid(),
                Registries.ITEM.getId(stack.getItem()),
                player.getWorld().getTime(),
                Registries.ENCHANTMENT.getId(de_instance.getEnchantment()),
                (short) de_instance.getLevel(),
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

        nbt.put(DurationEnchantImpl.KEY_DURATION_ENCHANTMENTS, de_nbt);

        return nbt;
    }

    public static DurationEnchantmentRegistry createFromNbt(NbtCompound nbt) {

        DurationEnchantmentRegistry state = new DurationEnchantmentRegistry();

        NbtList list = nbt.getCompound(DurationEnchantImpl.KEY_DURATION_ENCHANTMENTS).getList(KEY_CONCURRENT_LIST, NbtElement.COMPOUND_TYPE);
        if(CONCURRENT_DURATION_ENCHANTMENTS.isEmpty()){
            for(NbtElement instance_nbt: list){
                DurationEnchantmentInstance instance = DurationEnchantmentInstance.fromNBT((NbtCompound) instance_nbt);
                if(instance != null) CONCURRENT_DURATION_ENCHANTMENTS.put(instance.getInstance_id(), instance);
            }
        }

        return state;
    }

    private static final Type<DurationEnchantmentRegistry> type = new Type<>(DurationEnchantmentRegistry::new, DurationEnchantmentRegistry::createFromNbt, null);

    public static DurationEnchantmentRegistry getState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        DurationEnchantmentRegistry state = persistentStateManager.getOrCreate(type, DurationEnchantImpl.KEY_DURATION_ENCHANTMENTS);
        state.markDirty();
        return state;
    }


    public static class Identifier{

        private final UUID player_created;
        private final net.minecraft.util.Identifier stack;
        private final Long time_created;
        private final net.minecraft.util.Identifier enchantment;
        private final short level;
        private final UUID instance_id;

        Identifier(UUID player, net.minecraft.util.Identifier stack, Long time_created, net.minecraft.util.Identifier enchantment, short level, UUID instance_id){
            this.player_created = player;
            this.stack = stack;
            this.time_created = time_created;
            this.enchantment = enchantment;
            this.level = level;
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

        public short getLevel() { return level; }

        public UUID getInstance_id() {
            return instance_id;
        }
    }






}
