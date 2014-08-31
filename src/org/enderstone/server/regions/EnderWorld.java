package org.enderstone.server.regions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.entity.Entity;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.packet.play.PacketOutEntityDestroy;
import org.enderstone.server.packet.play.PacketOutEntityMetadata;
import org.enderstone.server.packet.play.PacketOutSoundEffect;
import org.enderstone.server.packet.play.PacketOutSpawnObject;
import org.enderstone.server.regions.generators.TimTest;
import org.enderstone.server.util.IntegerArrayComparator;

/**
 *
 * @author Fernando
 */
public class EnderWorld {

	private Long seed = null;
	private final RegionSet loadedChunks = new RegionSet();
	public final Map<EnderPlayer, RegionSet> players = new LinkedHashMap<>();
	private final ChunkGenerator generator = new TimTest();
	private final Random random = new Random();
	public static final int AMOUNT_OF_CHUNKSECTIONS = 16;
	public final Set<Entity> entities = new HashSet<>();

	public EnderChunk getOrCreateChunk(int x, int z) {
		EnderChunk r = getChunk(x, z);
		if (r != null) {
			return r;
		}
		BlockId[][] blocks = generator.generateExtBlockSections(this, new Random(), x, z, null);
		if (blocks == null) {
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}
		if (blocks.length != AMOUNT_OF_CHUNKSECTIONS) {
			blocks = new BlockId[AMOUNT_OF_CHUNKSECTIONS][];
		}

		short[][] id = new short[16][];
		byte[][] data = new byte[16][];
		for (int i = 0; i < blocks.length; i++) {

			if (blocks[i] != null) {
				id[i] = new short[4096];
				data[i] = new byte[4096];
				for (int j = 0; j < 4096; j++) {

					if (blocks[i][j] == null) {
						id[i][j] = BlockId.AIR.getId();
					} else {
						id[i][j] = blocks[i][j].getId();
					}
				}
			}
		}
		loadedChunks.add(r = new EnderChunk(x, z, id, data, new byte[16 * 16], new ArrayList<BlockData>()));
		return checkChunkPopulation(r);
	}

	public EnderChunk getChunk(int x, int z) {
		EnderChunk loaded = this.loadChunk(x, z);
		if (loaded != null) {
			return loaded;
		}
		return checkChunkPopulation(this.loadedChunks.get(x, z));
	}

	public EnderChunk loadChunk(int x, int z) {
		return checkChunkPopulation(null);
	}

	public void saveChunk(EnderChunk ender) {

	}

	private EnderChunk checkChunkPopulation(EnderChunk c) {
		if (c == null || c.hasPopulated) {
			return c;
		}
		boolean didPopulate = false;
		try {
			c.hasPopulated = true;
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					if (k == 0 && i == 0) {
						continue;
					}
					EnderChunk found = this.getChunk(i + c.getX(), k + c.getZ());
					if (found == null) {
						return c;
					}
				}
			}
			didPopulate = true;
			try {
				for (BlockPopulator blocks : generator.getDefaultPopulators(this)) {
					blocks.populate(this, random, c);
				}
			} catch (Exception e) {
				EnderLogger.exception(e);
			}
		} finally {
			c.hasPopulated = didPopulate;
		}
		return c;
	}

	public BlockId getBlockIdAt(int x, int y, int z) {
		return getOrCreateChunk(x >> 4, z >> 4).getBlock(x & 0xF, y & 0xFF, z & 0xF);
	}

	public byte getBlockDataAt(int x, int y, int z) {
		return getOrCreateChunk(x >> 4, z >> 4).getData(x & 0xF, y & 0xFF, z & 0xF);
	}

	public void setBlockAt(int x, int y, int z, BlockId id, byte data) {
		getOrCreateChunk(x >> 4, z >> 4).setBlock(x & 0xF, y & 0xFF, z & 0xF, id, data);
	}

	public void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius) {
		doChunkUpdatesForPlayer(player, informer, radius, false);
	}

	public void doChunkUpdatesForPlayer(EnderPlayer player, ChunkInformer informer, int radius, boolean force) {
		synchronized (informer) {
			RegionSet playerChunks = players.get(player);
			if (playerChunks == null) {
				players.put(player, playerChunks = new RegionSet());
			}
			int r2 = radius * 2 + 1;
			int px = player.getLocation().getBlockX() >> 4;
			int cx = (px) - radius;
			int mx = cx + r2;
			int pz = player.getLocation().getBlockZ() >> 4;
			int minz = (pz) - radius;
			int cz = minz;
			int mz = cz + r2;
			try {
				if (playerChunks.isEmpty()) {
					while (cx++ < mx) {
						for (cz = minz; cz < mz; cz++) {
							EnderChunk c = getOrCreateChunk(cx, cz);
							playerChunks.add(c);
							informer.sendChunk(c);
						}
					}
				} else {
					int maxSize = force ? Integer.MAX_VALUE : informer.maxChunks();
					int[][] chunkLoad = new int[(radius * 2) * (radius * 2) * 2][];
					int index = 0;
					Set<EnderChunk> copy = new RegionSet(playerChunks);

					for (; cx < mx; cx++) {
						for (cz = minz; cz < mz; cz++) {
							EnderChunk tmp = getOrCreateChunk(cx, cz);
							if (!copy.contains(tmp)) {
								chunkLoad[index++] = new int[]{cx, cz};
							} else {
								copy.remove(tmp);
							}
						}
					}
					Iterator<EnderChunk> loop = copy.iterator();
					while (loop.hasNext()) {
						EnderChunk i = loop.next();
						playerChunks.remove(i);
						informer.removeChunk(i);
					}
					Arrays.sort(chunkLoad, 0, index, new IntegerArrayComparator(px, pz));
					if (maxSize < chunkLoad.length) chunkLoad[maxSize] = null;
					index = 0;
					for (int[] l : chunkLoad) {
						if (l == null) {
							break;
						}
						cx = l[0];
						cz = l[1];
						EnderChunk c = getOrCreateChunk(cx, cz);
						playerChunks.add(c);
						informer.sendChunk(c);
						index++;
					}
					if (index > 0) EnderLogger.debug("Send " + index + " chunks to player: " + player.getName());
				}
			} finally {
				informer.done();
			}
		}
	}

	public long getSeed() {
		if (seed == null) {
			seed = Main.random.nextLong();
		}
		return seed;
	}

	public static interface ChunkInformer {

		public void sendChunk(EnderChunk chunk);

		public void removeChunk(EnderChunk chunk);

		public void done();

		public int maxChunks();
	}

	public void broadcastSound(String soundName, int x, int y, int z, float volume, byte pitch, Location loc, EnderPlayer exceptOne) {
		PacketOutSoundEffect packet = new PacketOutSoundEffect(soundName, x, y, z, volume, pitch);
		for (EnderPlayer ep : Main.getInstance().onlinePlayers) {
			if ((!ep.equals(exceptOne)) && loc.isInRange(15, ep.getLocation())) {
				ep.getNetworkManager().sendPacket(packet);
			}
		}
	}

	public void addEntity(Entity e) {
		this.entities.add(e);
	}

	public void updateEntities(List<EnderPlayer> onlinePlayers){
		for(Entity e : this.entities){
			e.updatePlayers(onlinePlayers);
		}
	}

	public Block getBlock(int x, int y, int z) {
		return new EnderBlock(x, y, z, this);
	}

}
