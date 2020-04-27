package com.github.derrop.proxy.util.serialize;

import com.github.derrop.proxy.api.network.wrapper.ProtoBuf;
import com.github.derrop.proxy.api.util.ItemStack;
import com.github.derrop.proxy.util.Rotations;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Minecrafts DataWatcher
 */
public class MinecraftSerializableObjectList {

    private final Map<Integer, SerializableObject> objects = new HashMap<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Collection<SerializableObject> getObjects() {
        return Collections.unmodifiableCollection(this.objects.values());
    }

    public <T> void addObject(int id, T object) {
        int typeId = SerializableObject.getDataType(object.getClass());

        if (typeId == -1) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        } else if (id > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        } else if (this.objects.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        } else {
            SerializableObject serializableObject = new SerializableObject(typeId, id, object);
            this.lock.writeLock().lock();
            this.objects.put(id, serializableObject);
            this.lock.writeLock().unlock();
        }
    }

    public void addEmptyObjectByDataType(int id, int type) {
        SerializableObject serializableObject = new SerializableObject(type, id, null);
        this.lock.writeLock().lock();
        this.objects.put(id, serializableObject);
        this.lock.writeLock().unlock();
    }

    public byte getByte(int id) {
        return (byte) this.getObjectValue(id).orElse(-1);
    }

    public short getShort(int id) {
        return (short) this.getObjectValue(id).orElse(-1);
    }

    public int getInt(int id) {
        return (int) this.getObjectValue(id).orElse(-1);
    }

    public float getFloat(int id) {
        return (float) this.getObjectValue(id).orElse(-1);
    }

    public String getString(int id) {
        return (String) this.getObjectValue(id).orElse(null);
    }

    public ItemStack getItemStack(int id) {
        return (ItemStack) this.getObjectValue(id).orElse(null);
    }

    public Rotations getRotations(int id) {
        return (Rotations) this.getObjectValue(id).orElse(null);
    }

    private Optional<SerializableObject> getObject(int id) {
        this.lock.readLock().lock();
        SerializableObject serializableObject;

        try {
            serializableObject = this.objects.get(id);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }

        this.lock.readLock().unlock();
        return Optional.ofNullable(serializableObject);
    }

    private Optional<Object> getObjectValue(int id) {
        return this.getObject(id).flatMap(SerializableObject::getValue);
    }

    public void applyUpdate(Collection<SerializableObject> objects) {
        this.lock.writeLock().lock();

        for (SerializableObject object : objects) {
            SerializableObject previous = this.objects.get(object.getId());
            if (previous != null) {
                previous.setValue(object.getValue().orElse(null));
            } else {
                this.objects.put(object.getId(), object);
            }
        }

        this.lock.writeLock().unlock();
    }

    public static void writeList(ProtoBuf buffer, Collection<SerializableObject> objects) {
        if (objects != null) {
            for (SerializableObject object : objects) {
                object.write(buffer);
            }
        }
        buffer.writeByte(SerializableObject.END);
    }

    public static Collection<SerializableObject> readList(ProtoBuf buffer) {
        Collection<SerializableObject> objects = new ArrayList<>();
        SerializableObject object;
        while ((object = SerializableObject.read(buffer)) != null) {
            objects.add(object);
        }

        return objects;
    }

}
