/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.enderstone.server.regions;

import java.util.Random;

/**
 *
 * @author Fernando
 */
public interface BlockPopulator {

    /**
     * Populates an area of blocks at or around the given chunk.
     * <p>
     * The chunks on each side of the specified chunk must already exist; that
     * is, there must be one north, east, south and west of the specified chunk.
     * The "corner" chunks may not exist, in which scenario the populator should
     * record any changes required for those chunks and perform the changes when
     * they are ready.
     *
     * @param world The world to generate in
     * @param random The random generator to use
     * @param source The chunk to generate for
     */
    public abstract void populate(EnderWorld world, Random random, EnderChunk source);
}
