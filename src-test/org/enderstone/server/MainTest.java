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
package org.enderstone.server;

import java.util.Arrays;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.util.FixedSizeList;
import org.enderstone.server.util.IntegerArrayComparator;
import org.junit.Assert;

/**
 *
 * @author Fernando
 */
public class MainTest {

	@org.junit.Test
	public void testAdvancedMessageToPlainText() {
		Assert.assertEquals("test (Playername)", new AdvancedMessage().addPart("test (").addPart("Playername").addPart(")").build().toPlainText());
	}

	@org.junit.Test
	public void testBlockId() {
		for (BlockId id : BlockId.values()) {
			Assert.assertEquals(id.name(), id, BlockId.byId(id.getId()));
		}
	}

	@org.junit.Test
	public void testChunkSending() {
		int[][] test = new int[100][];
		int size = 0;
		for (int x = -2; x <= 2; x++)
			for (int z = -2; z <= 2; z++)
				test[size++] = new int[]{x, z};
		Arrays.sort(test, 0, size, new IntegerArrayComparator(0, 0));
		Assert.assertArrayEquals(test[0], new int[]{0, 0});
	}

	@org.junit.Test
	public void testItemStackClone() {
		ItemStack item = new ItemStack(BlockId.COOKIE.getId(), (byte) 1, (short) 0);
		Assert.assertEquals(item.clone(), item);
	}
}
