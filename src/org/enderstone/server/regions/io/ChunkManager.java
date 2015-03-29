/*
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.enderstone.server.regions.io;

import java.io.File;
import java.util.List;
import java.util.Set;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderChunk;

public class ChunkManager {
	
	private ChunkGenerator generator;
	
	private File regionDirectory;
	
	private List<EnderChunk> unusedChunks;
	
	private Set<EnderChunk> loadedChunks;
	
	public EnderChunk getChunk(int x, int z) {
		return null;
	}
	
	public void saveChunks() {
		
	}
}
