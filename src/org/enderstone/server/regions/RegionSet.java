/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.bigteddy98.mcserver.regions;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 *
 * @author Fernando
 */
public class RegionSet extends AbstractSet<EnderChunk> {

	Node[][] chunkBuckets = new Node[16][16];

	private static class Node {

		public Node(int regionX, int regionZ) {
			this.regionX = regionX;
			this.regionZ = regionZ;
		}

		private int regionX, regionZ;
		private Node next;
		private EnderChunk[] regionChunks = new EnderChunk[32 * 32];

	}

	@Override
	public boolean add(EnderChunk c) {
		int x = c.getX();
		int z = c.getZ();
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node prev;
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n == null) {
			n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)] = new Node(rX, rZ);
		}
		do {
			prev = n;
			if (n.regionX == rX && n.regionZ == rZ) {
				n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = c;
				return true;
			}
		} while ((n = n.next) != null);

		prev.next = new Node(rX, rZ);
		prev.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = c;
		return false;
	}

	@Override
	public Iterator<EnderChunk> iterator() {
		return new Iterator<EnderChunk>() {

			EnderChunk next;
			int i3 = Integer.MAX_VALUE;
			Node n2;
			int i1, i2;
			boolean hasNext = true;

			private boolean calculateNext() {
				if (hasNext == false) {
					return false;
				}
				mainLoop: while (next == null) {
					if (i3 >= 32 * 32 - 1) {
						i3 = 0;

						if (n2 != null) {
							n2 = n2.next;
						}
						while (n2 == null) {
							n2 = RegionSet.this.chunkBuckets[i1][i2];
							if (i1 >= 15) {
								if (i2 >= 15) {
									break mainLoop;
								}
								i2++;
								i1 = 0;
							} else {
								i1++;
							}
						}
					}
					next = n2.regionChunks[i3];
					i3++;
				}
				if (next == null) {
					hasNext = false;
				}
				return next != null;

			}

			@Override
			public boolean hasNext() {
				if (next == null) {
					return calculateNext();
				}
				return true;
			}

			@Override
			public EnderChunk next() {
				if (next == null) {
					if (!calculateNext()) {
						throw new IllegalStateException("No Chunks");
					}
				}
				EnderChunk n = next;
				next = null;
				return n;
			}

			@Override
			public void remove() {
				throw new IllegalStateException("NOPE NOPE NOPE");
			}
		};
	}

	@Override
	public int size() {
		int size = 0;
		for (Node[] n : chunkBuckets) {
			for (Node n1 : n) {
				if (n1 == null) {
					continue;
				}
				do {
					for (EnderChunk c : n1.regionChunks) {
						if (c != null) {
							size++;
						}
					}
				} while ((n1 = n1.next) != null);
			}
		}
		return size;
	}

	@Override
	public void clear() {
		chunkBuckets = new Node[16][16];
	}

	@Override
	public boolean remove(Object o) {
		EnderChunk c = (EnderChunk) o;
		int x = c.getX();
		int z = c.getZ();
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					boolean contains = n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] != null;
					n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = null;
					return contains;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	@Override
	public boolean contains(Object o) {
		EnderChunk c = (EnderChunk) o;
		int x = c.getX();
		int z = c.getZ();
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					return n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] != null;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	public boolean contains(int x, int z) {
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					return n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] == null;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	public EnderChunk get(int x, int z) {
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					return n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32];
				}
			} while ((n = n.next) != null);
		}
		return null;
	}

	protected static int maskCordinate(int c) {
		return c & 0xF;
	}

	private static int calculateChunkPos(int rawChunkLocation) {
		rawChunkLocation %= 32;
		if (rawChunkLocation < 0) {
			rawChunkLocation += 32;
		}
		return rawChunkLocation;
	}

	protected static int calculateRegionPos(int raw) {
		raw /= 32;
		if (raw < 0) {
			raw += 32;
		}
		return raw;
	}
}
