/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.entity.pathfinding;

import java.util.HashMap;

/**
 *
 * @author gyroninja
 */
public class PathTileList extends HashMap<String, PathTile> {

	public void add(PathTile tile) {

		this.put(tile.toString(), tile);
	}

	public void add(PathTile tile, boolean overrite) {

		if (containsKey(tile.toString())) {

			if (overrite) {

				add(tile);
			}
		}

		else {

			add(tile);
		}
	}
}
