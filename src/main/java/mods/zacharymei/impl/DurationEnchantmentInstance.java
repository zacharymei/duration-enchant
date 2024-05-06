package mods.zacharymei.impl;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class DurationEnchantmentInstance {

    public static final String KEY_INSTANCE_ID = "de.instance_id";
    public static final String KEY_ENCHANTMENT_ID = "de.enchantment_id";
    public static final String KEY_LEVEL = "de.level";
    public static final String KEY_EXIST_LEVEL = "de.exist_level";
    public static final String KEY_DURATION = "de.duration";
    public static final String KEY_TIMEOUT = "de.timeout";
    public static final String KEY_CREATED_TIME = "de.created_time";


    private UUID instance_id;
    private Identifier enchantment_ID;
    private short level;
    private short exist_level;
    private int duration;
    private int timeout;
    private long created_time;

    private DurationEnchantmentInstance(){}

    private DurationEnchantmentInstance(UUID instance_id, Identifier enchantment_ID, short level, short exist_level, int duration, int timeout, long created_time) {
        this.instance_id = instance_id;
        this.enchantment_ID = enchantment_ID;
        this.level = level;
        this.exist_level = exist_level;
        this.duration = duration;
        this.timeout = timeout;
        this.created_time = created_time;
    }

    DurationEnchantmentInstance(DurationEnchant.Builder builder){
        this.enchantment_ID = EnchantmentHelper.getEnchantmentId(builder.getEnchantment());
        this.level = builder.getLevel();
        this.exist_level = builder.getExist_level();
        this.duration = builder.getDuration();
        this.timeout = this.duration;
        this.created_time = builder.getCreated_time();
        this.instance_id = UUID.randomUUID();
    }

    NbtCompound createNBT(){
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid(KEY_INSTANCE_ID, this.instance_id);
        nbt.putString(KEY_ENCHANTMENT_ID, enchantment_ID.toString());
        nbt.putShort(KEY_LEVEL, this.level);
        nbt.putShort(KEY_EXIST_LEVEL, this.exist_level);
        nbt.putInt(KEY_DURATION, this.duration);
        nbt.putInt(KEY_TIMEOUT, this.timeout);
        nbt.putLong(KEY_CREATED_TIME, this.created_time);

        return nbt;
    }



    @Nullable
    static DurationEnchantmentInstance fromNBT(NbtCompound nbt){

        UUID instance_id = nbt.getUuid(KEY_INSTANCE_ID);

        String string = nbt.getString(KEY_ENCHANTMENT_ID);
        Identifier enchantment_id = Identifier.tryParse(string);
        Enchantment enchantment = Registries.ENCHANTMENT.get(Identifier.tryParse(string));
        if(enchantment == null) return null;

        short level = nbt.getShort(KEY_LEVEL);
        short exist_level = nbt.getShort(KEY_EXIST_LEVEL);
        int duration = nbt.getInt(KEY_DURATION);
        int timeout = nbt.getInt(KEY_TIMEOUT);
        int created_time = nbt.getInt(KEY_CREATED_TIME);

        return new DurationEnchantmentInstance(instance_id, enchantment_id, level, exist_level, duration, timeout, created_time);
    }

    void update(Runnable timeoutCallback){
        this.updateTimeout();
        if(this.timeout <= 0) timeoutCallback.run();
    }


    private void updateTimeout(){
        this.timeout --;
    }



    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(o instanceof DurationEnchantmentInstance de_instance){
            return EnchantmentHelper.getEnchantmentId(de_instance.getEnchantment()) == this.enchantment_ID
                    && de_instance.getLevel() == this.level
                    && de_instance.getExist_level() == this.exist_level
                    && de_instance.getDuration() == this.duration
                    && de_instance.getCreated_time() == this.created_time;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance_id);
    }

    public UUID getInstance_id() {
        return instance_id;
    }

    public Enchantment getEnchantment(){
        return Registries.ENCHANTMENT.get(this.enchantment_ID);
    }

    public int getLevel() {
        return this.level;
    }

    public short getExist_level() {
        return exist_level;
    }

    public int getDuration(){
        return this.duration;
    }

    public int getTimeout() {
        return timeout;
    }

    public long getCreated_time() {
        return created_time;
    }









}
