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

import java.io.IOException;
import java.util.Arrays;

import org.enderstone.server.api.Location;
import org.enderstone.server.api.messages.AdvancedMessage;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.util.IntegerArrayComparator;

public class Test {

	public static void main(String[] args) throws IOException {
		{
			System.out.println("Test 1: ");
			System.out.println(Arrays.asList(BlockId.values()));
		}

		{
			System.out.println("Test 2: ");
			System.out.println(new AdvancedMessage().addPart("test (").addPart("Playername").addPart(")").build().toPlainText());
		}

		{
			System.out.println("Test 3: ");
			int[][] test = new int[100][];
			int size = 0;
			for (int x = -2; x <= 2; x++)
				for (int z = -2; z <= 2; z++)
					test[size++] = new int[] { x, z };
			Arrays.sort(test, 0, size, new IntegerArrayComparator(0, 0));
			for (int i = 0; i < size; i++)
				System.out.println(test[i][0] + "," + test[i][1]);
		}
		{
			System.out.println("Test 4: ");
			Location loc1 = new Location(null, 0, 0, 0, 0F, 0F);
			Location loc2 = new Location(null, -1, 0, 1, 0F, 0F);
			System.out.println("Yaw should be 45 degrees and is " + Location.calcYaw(loc1, loc2));
		}
	}
}
