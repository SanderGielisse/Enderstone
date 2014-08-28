package org.enderstone.server.regions;

import org.enderstone.server.inventory.ItemStack;

/**
 *
 * @author Fernando
 */
public interface BlockData {
    public ItemStack getDrop();
    public int getX();
    public int getY();
    public int getZ();
}
