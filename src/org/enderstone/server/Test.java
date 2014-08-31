package org.enderstone.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.enderstone.server.chat.AdvancedMessage;
import org.enderstone.server.entity.DataWatcher;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.Packet;
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
			System.out.println("Test 4.");
			
			DataWatcher watcher = new DataWatcher();
			watcher.watch(10, new ItemStack((short) 2, (byte) 6, (short) 1));
			
			ByteBuf buf = Unpooled.buffer();
			
			Packet.writeDataWatcher(watcher, buf);

			DataWatcher watcher2 = Packet.readDataWatcher(buf);
			System.out.println(((ItemStack) watcher2.getWatchedCopy().get(10)).getAmount());
		}
	}
}
