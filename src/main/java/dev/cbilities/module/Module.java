package dev.cbilities.module;

/**
 * A self-contained feature. Add a new one by implementing this interface
 * and registering it in {@code CBilitiesPlugin}.
 */
public interface Module {

    /** Unique id, used as the key under {@code modules} in config.yml. */
    String id();

    void enable();

    void disable();
}
