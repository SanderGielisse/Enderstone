package org.enderstone.server;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.enderstone.server.chat.AdvancedMessage;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.util.IntegerArrayComparator;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		{
		System.out.println("Test 1: ");
		System.out.println(Arrays.asList(BlockId.values()));
		}

		{
		System.out.println("Test 2: ");
		System.out.println(new AdvancedMessage().
				addPart("test (").
				addPart("Playername").
				addPart(")").
				build().toPlainText()
		);
		}

		{
			System.out.println("Test 3: ");
			int[][] test = new int[100][];
			int size = 0;
			for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) test[size++] = new int[]{x, z};
			Arrays.sort(test, 0, size, new IntegerArrayComparator(0, 0));
			for (int i = 0; i < size; i++) System.out.println(test[i][0] + "," + test[i][1]);
		}
	}
}
