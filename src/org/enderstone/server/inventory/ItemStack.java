/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.inventory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.enderstone.server.EnderLogger;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

/**
 *
 * @author Fernando
 */

public class ItemStack {

	private short blockId;
	private byte amount;
	private short damage;
	private short nbtLength;
	private byte[] nbtData;
	private CompoundTag compoundTag;

	public ItemStack(short blockId, byte amount, short damage) {
		this.blockId = blockId;
		this.amount = amount;
		this.damage = damage;
		this.updateNBTData();
	}

	public void updateNBTData() {
		Map<String, Tag> map = new HashMap<>();

		map.put("Count", new ByteTag("Count", this.getAmount()));
		map.put("Damage", new ShortTag("Damage", this.getDamage()));
		map.put("id", new StringTag("id", "" + getBlockId())); //TODO -> String in 1.8+

		this.compoundTag = new CompoundTag("", map);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try (NBTOutputStream stream = new NBTOutputStream(outStream)) {
			stream.writeTag(compoundTag);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.nbtData = outStream.toByteArray();
		this.nbtLength = (short) nbtData.length;
	}

	public short getBlockId() {
		return blockId;
	}

	public void setBlockId(short blockId) {
		this.blockId = blockId;
	}

	public byte getAmount() {
		return amount;
	}

	public void setAmount(byte amount) {
		this.amount = amount;
	}

	public short getDamage() {
		return damage;
	}

	public void setDamage(short damage) {
		this.damage = damage;
	}

	public short getNbtLength() {
		return nbtLength;
	}

	public void setNbtLength(short nbtLength) {
		this.nbtLength = nbtLength;
	}

	public byte[] getNbtData() {
		return nbtData;
	}

	public void setNbtData(byte[] nbtData) {
		this.nbtData = nbtData;
	}

	public CompoundTag getCompoundTag() {
		return compoundTag;
	}

}
