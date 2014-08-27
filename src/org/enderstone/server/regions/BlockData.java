/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
