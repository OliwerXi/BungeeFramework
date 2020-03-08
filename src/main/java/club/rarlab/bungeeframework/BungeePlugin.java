package club.rarlab.bungeeframework;

import club.rarlab.bungeeframework.serializer.Persist;
import jdk.nashorn.internal.objects.annotations.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    /** FIELDS **/
    private static BungeePlugin instance;
    private static Persist persist;

    /** Triggered when the plugin enables. **/
    @Override
    public void onEnable() {
        instance = this;
        this.getDataFolder().mkdirs();
        persist = new Persist();
    }

    /**
     * Get the persist instance.
     *
     * @return Persist instance.
     */
    @Getter
    public static Persist getPersist() {
        return persist;
    }

    /**
     * Get this class's instance.
     *
     * @return BungeePlugin instance.
     */
    @Getter
    public static BungeePlugin getInstance() {
        return instance;
    }
}