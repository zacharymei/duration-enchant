package mods.zacharymei.de.impl;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DurationEnchantImpl {

    public static final String KEY_DURATION_ENCHANTMENTS = "de.duration_enchant";
    public static final String KEY_PLAYER_CREATED = "player_created";
    public static final String KEY_TIME_CREATED = "time_created";
    public static final String KEY_ENCHANTMENT = "enchantment";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_INSTANCE_ID = "instance_id";


    public static void create(PlayerEntity player, ItemStack stack, Builder builder){

        DurationEnchantmentInstance instance = appendDurationEnchantment(stack, builder.created_player(player).created_time(player.getWorld().getTime()), player.getServer());
        if(instance == null) return;
        DurationEnchantmentRegistry ter = DurationEnchantmentRegistry.getState(Objects.requireNonNull(player.getServer()));
        DurationEnchantmentRegistry.Identifier key = ter.register(player, stack, instance);
        writeNBT(stack, key);

        player.sendMessage(
                Text.literal("You get " + instance.getDuration()/20 + " seconds ")
                        .append(instance.getEnchantment().getName(instance.getLevel())));

    }

    @Nullable
    private static DurationEnchantmentInstance appendDurationEnchantment(ItemStack stack, Builder builder, MinecraftServer server){

        DurationEnchantmentInstance exist_instance;
        if((exist_instance = hasDurationEnchantment(stack.getOrCreateNbt(), builder.getEnchantment())) != null){
            if(exist_instance.getLevel() >= builder.getLevel()) return null;
            DurationEnchantmentRegistry.getState(server).forceTimeout(stack, exist_instance.getInstance_id());
        }


        Map<Enchantment, Integer> exist_enchantments = EnchantmentHelper.fromNbt(stack.getEnchantments());
        DurationEnchantmentInstance instance = null;

        if(exist_enchantments.containsKey(builder.getEnchantment())){
            if(exist_enchantments.get(builder.getEnchantment()) >=  builder.getLevel()) return null;
            instance = builder.exist_level(exist_enchantments.get(builder.getEnchantment())).build();

            DurationEnchantmentHelper.removeExistEnchantment(stack, builder.getEnchantment());
        }

        if(instance == null) instance = builder.build();
        stack.addEnchantment(instance.getEnchantment(), instance.getLevel());

        return instance;
    }


    private static void writeNBT(ItemStack stack, DurationEnchantmentRegistry.Identifier entry){
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid(KEY_PLAYER_CREATED, entry.getPlayer_created());
        nbt.putLong(KEY_TIME_CREATED, entry.getTime_created());
        nbt.putString(KEY_ENCHANTMENT, entry.getEnchantment().toString());
        nbt.putShort(KEY_LEVEL, entry.getLevel());
        nbt.putUuid(KEY_INSTANCE_ID, entry.getInstance_id());
        NbtList list = stack.getOrCreateNbt().getList(KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
        list.add(nbt);
        stack.getOrCreateNbt().put(KEY_DURATION_ENCHANTMENTS, list);
    }

    @Nullable
    static DurationEnchantmentRegistry.Identifier fromNBT(ItemStack stack, NbtCompound nbt){

        Identifier en_id = Identifier.tryParse(nbt.getString(KEY_ENCHANTMENT));
        if(en_id == null) return null;

        return new DurationEnchantmentRegistry.Identifier(
                nbt.getUuid(KEY_PLAYER_CREATED),
                Registries.ITEM.getId(stack.getItem()),
                nbt.getLong(KEY_TIME_CREATED),
                Identifier.tryParse(nbt.getString(KEY_ENCHANTMENT)),
                nbt.getShort(KEY_LEVEL),
                nbt.getUuid(KEY_INSTANCE_ID)
        );
    }

    private static void removeNBT(ItemStack stack, UUID instance_id){
        NbtList de_list = stack.getOrCreateNbt().getList(KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
        NbtList list = new NbtList();

        for(NbtElement e: de_list){
            if(!((NbtCompound) e).getUuid(KEY_INSTANCE_ID).equals(instance_id)) list.add(e);
        }

        if(!list.isEmpty()) stack.setSubNbt(KEY_DURATION_ENCHANTMENTS, list);
        else stack.removeSubNbt(KEY_DURATION_ENCHANTMENTS);
    }

    @Nullable
    static DurationEnchantmentInstance hasDurationEnchantment(NbtCompound stack_nbt, Enchantment enchantment){
        NbtList exist_DEs = stack_nbt.getList(KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
        for(NbtElement de : exist_DEs){
            Identifier en = Identifier.tryParse(((NbtCompound) de).getString(KEY_ENCHANTMENT));
            if(en != null && en.equals(EnchantmentHelper.getEnchantmentId(enchantment))){
                UUID instance_id = ((NbtCompound) de).getUuid(KEY_INSTANCE_ID);
                if(DurationEnchantmentRegistry.getConcurrentInstance(instance_id) != null) return DurationEnchantmentRegistry.getConcurrentInstance(instance_id);
            }
        }
        return null;
    }

    static void timeoutItemInfo(ItemStack stack, DurationEnchantmentInstance de_instance){

        if(de_instance == null) return;

        Enchantment enchantment = de_instance.getEnchantment();
        DurationEnchantmentHelper.removeExistEnchantment(stack, enchantment);
        if(de_instance.getExist_level() != 0) stack.addEnchantment(de_instance.getEnchantment(), de_instance.getExist_level());
        removeNBT(stack, de_instance.getInstance_id());
    }

    public static List<DurationEnchantmentRegistry.Identifier> getItemDurationEnchantmentInfo(ItemStack stack){
        List<DurationEnchantmentRegistry.Identifier> list = new ArrayList<>();
        NbtList nbt_list = stack.getOrCreateNbt().getList(KEY_DURATION_ENCHANTMENTS, NbtElement.COMPOUND_TYPE);
        for(NbtElement e: nbt_list){
            list.add(fromNBT(stack, (NbtCompound) e));
        }
        return list;
    }


    public static Builder Builder(Enchantment enchantment){
        return new Builder(enchantment);
    }




    public static class Builder{

        private final Enchantment enchantment;

        private int duration;
        private short level;
        private short exist_level;

        private UUID created_player;
        private long created_time;

        private final int default_duration = 300;

        public Builder(Enchantment enchantment) {
            this.enchantment = enchantment;
            this.duration = default_duration;
            this.level = (short) getRandomLevel(enchantment);
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public short getLevel(){
            return this.level;
        }

        public int getDuration() {
            return duration;
        }

        public short getExist_level() {
            return exist_level;
        }

        public UUID getCreated_player() {
            return created_player;
        }

        public long getCreated_time() {
            return created_time;
        }

        public Builder duration(int duration){
            if(duration > 0) this.duration =  duration;
            return this;
        }

        public Builder level(int level){
            if(level > 0) this.level = (short) level;
            return this;
        }

        private Builder exist_level(int level){
            this.exist_level = (short) (Math.max(level, 0));
            return this;
        }

        private Builder created_player(PlayerEntity player){
            this.created_player = player.getUuid();
            return this;
        }

        private Builder created_time(long world_time){
            this.created_time = (world_time >= 0)? world_time:-1;
            return this;
        }

        private DurationEnchantmentInstance build(){
            return new DurationEnchantmentInstance(this);
        }

        private static int getRandomLevel(Enchantment enchantment){
            return (new Random()).nextInt(enchantment.getMaxLevel()) + 1;
        }
    }


}
