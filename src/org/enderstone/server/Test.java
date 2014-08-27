package org.enderstone.server;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.enderstone.server.regions.BlockId;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println("Test 1: ");
		System.out.println(Arrays.asList(BlockId.values()));
	}
}
