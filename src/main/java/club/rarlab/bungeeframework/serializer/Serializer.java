package club.rarlab.bungeeframework.serializer;

import club.rarlab.bungeeframework.BungeePlugin;

/**
 * @author SavageLabs {@code https://github.com/SavageLabs}
 */
public class Serializer {
    /* FIELDS */
    private boolean data;

    /** CONSTRUCTORS **/
    public Serializer(boolean data) { this.data = data; }
    public Serializer() { this.data = false; }

    /** Saves your class to a .json file. **/
    public void save(Object instance) {
        BungeePlugin.getPersist().save(data, instance);
    }

    /** Loads your class from a json file **/
    public <T> T load(T def, Class<T> clazz, String name) {
        return BungeePlugin.getPersist().loadOrSaveDefault(data, def, clazz, name);
    }
}