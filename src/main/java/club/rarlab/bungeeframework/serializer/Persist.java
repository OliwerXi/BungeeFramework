package club.rarlab.bungeeframework.serializer;

import club.rarlab.bungeeframework.BungeePlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.prosavage.baseplugin.serializer.typeadapter.EnumTypeAdapter;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * @author SavageLabs {@code https://github.com/SavageLabs}
 */
public class Persist {
    private final Gson gson = buildGson().create();
    private final Gson dataGson = buildDataGson().create();

    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //

    public static String getName(Object o) {
        return getName(o.getClass());
    }

    public static String getName(Type type) {
        return getName(type.getClass());
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }


    private GsonBuilder buildDataGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object?
    // ------------------------------------------------------------ //

    public File getFile(boolean data, String name) {
        File dataFolder = BungeePlugin.getInstance().getDataFolder();
        if (data) {
            dataFolder = new File(dataFolder, "/data");
            dataFolder.mkdirs();
        }
        return new File(dataFolder, name + ".json");
    }

    public File getFile(Class<?> clazz) {
        return getFile(false, getName(clazz));
    }

    public File getFile(boolean data, Object obj) {
        return getFile(data, getName(obj));
    }

    public File getFile(Type type) {
        return getFile(false, getName(type));
    }


    // NICE WRAPPERS

    public <T> T loadOrSaveDefault(T def, Class<T> clazz) {
        return loadOrSaveDefault(def, clazz, getFile(clazz));
    }

    public <T> T loadOrSaveDefault(boolean data, T def, Class<T> clazz, String name) {
        return loadOrSaveDefault(def, clazz, getFile(data, name));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, File file) {
        if (!file.exists()) {
            BungeePlugin.getInstance().getLogger().info("Creating default: " + file);
            this.save(false, def, file);
            return def;
        }

        T loaded = this.load(clazz, file);

        if (loaded == null) {
            BungeePlugin.getInstance().getLogger().warning("Using default as I failed to load: " + file);

            // backup bad file, so user can attempt to recover their changes from it
            File backup = new File(file.getPath() + "_bad");
            if (backup.exists()) {
                backup.delete();
            }
            BungeePlugin.getInstance().getLogger().warning("Backing up copy of bad file to: " + backup);
            file.renameTo(backup);

            return def;
        }

        return loaded;
    }

    // SAVE


    public boolean save(boolean data, Object instance) {
        return save(data, instance, getFile(data, instance));
    }

    public boolean save(boolean data, Object instance, String name) {
        return save(data, instance, getFile(false, name));
    }

    public boolean save(boolean data, Object instance, File file) {
        Gson gson;
        if (data) {
            gson = this.dataGson;
        } else {
            gson = this.gson;
        }
        return DiscUtil.writeCatch(file, gson.toJson(instance), true);
    }

    // LOAD BY CLASS

    public <T> T load(Class<T> clazz) {
        return load(clazz, getFile(clazz));
    }

    public <T> T load(Class<T> clazz, String name) {
        return load(clazz, getFile(false, name));
    }

    public <T> T load(Class<T> clazz, File file) {
        String content = DiscUtil.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return gson.fromJson(content, clazz);
        } catch (Exception ex) {
            BungeePlugin.getInstance().getLogger().warning(ex.getMessage());
        }

        return null;
    }


    // LOAD BY TYPE
    @SuppressWarnings("unchecked")
    public <T> T load(Type typeOfT, String name) {
        return (T) load(typeOfT, getFile(false, name));
    }

    @SuppressWarnings("unchecked")
    public <T> T load(Type typeOfT, File file) {
        String content = DiscUtil.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return (T) gson.fromJson(content, typeOfT);
        } catch (Exception ex) {    // output the error message rather than full stack trace; error parsing the file, most likely
            BungeePlugin.getInstance().getLogger().warning(ex.getMessage());
        }
        return null;
    }
}