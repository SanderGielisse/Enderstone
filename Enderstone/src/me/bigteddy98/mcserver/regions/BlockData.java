/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.bigteddy98.mcserver.regions;

import me.bigteddy98.mcserver.inventory.ItemStack;

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