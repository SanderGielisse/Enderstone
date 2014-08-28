package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.EnderLogger;
import org.enderstone.server.Location;
import org.enderstone.server.Main;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;
import org.enderstone.server.regions.BlockId;

public class PacketInBlockPlacement extends Packet {

	private int x;
	private byte y;
	private int z;
	private byte direction;
	private ItemStack heldItem;
	private byte cursorX;
	private byte cursorY;
	private byte cursorZ;

	@Override
	public void read(ByteBuf buf) throws Exception {
		this.x = buf.readInt();
		this.y = buf.readByte();
		this.z = buf.readInt();
		this.direction = buf.readByte();
		this.heldItem = readItemStack(buf);
		this.cursorX = buf.readByte(); // The position of the crosshair on the
										// block
		this.cursorY = buf.readByte();
		this.cursorZ = buf.readByte();
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws Exception {
		return (getIntSize() * 2) + 5 + getItemStackSize(this.heldItem) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x08;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		if (getHeldItem().getBlockId() == -1) {
			return;
		}
		Main.getInstance().sendToMainThread(new Runnable() {

			@Override
			public void run() {
				int x = getX();
				int y = getY();
				int z = getZ();

				short idAt = Main.getInstance().mainWorld.getBlockIdAt(x, y, z).getId();
				if (idAt == BlockId.LEVER.getId() || idAt == BlockId.BUTTON.getId()) {

				}

				byte direct = getDirection();
				if (direct == 0) {
					y--;
				} else if (direct == 1) {
					y++;
				} else if (direct == 2) {
					z--;
				} else if (direct == 3) {
					z++;
				} else if (direct == 4) {
					x--;
				} else if (direct == 5) {
					x++;
				}

				Location loc;
				if (networkManager.player.getLocation().isInRange(6, loc = new Location("", getX(), getY(), getZ(), 0F, 0F))) {
					short id = getHeldItem().getBlockId();
					if (id == 50 || id == 76 || id == 69) {

						/*byte datavalue = 0;

						if (direct == 0) {
							datavalue = 0;
						} else if (direct == 1) {
							datavalue = 5;
						} else if (direct == 2) {
							datavalue = 4;
						} else if (direct == 3) {
							datavalue = 3;
						} else if (direct == 4) {
							datavalue = 2;
						} else if (direct == 5) {
							datavalue = 1;
						}*/

						//datavalue = (byte) (Main.random.nextInt(10) + 6);

						Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.byId(getHeldItem().getBlockId()), (byte) 0);
					} else {
						Main.getInstance().mainWorld.setBlockAt(x, y, z, BlockId.byId(getHeldItem().getBlockId()), (byte) getHeldItem().getDamage());
					}
				}
				Main.getInstance().mainWorld.broadcastSound("dig.grass", x, y, z, 1F, (byte) 63, loc, null);
			}
		});
	}

	public int getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public byte getDirection() {
		return direction;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}

	public byte getCursorX() {
		return cursorX;
	}

	public byte getCursorY() {
		return cursorY;
	}

	public byte getCursorZ() {
		return cursorZ;
	}
}
