/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enderstone.server.inventory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

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

	public ItemStack(short blockId, byte amount, short damage, short nbtLength, byte[] nbtData) {
		this.blockId = blockId;
		this.amount = amount;
		this.damage = damage;
		this.nbtLength = nbtLength;
		this.nbtData = nbtData;

		if (nbtLength <= 0) {
			return;
		}

		try (NBTInputStream in = new NBTInputStream(new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(nbtData))))) {
			CompoundTag itemstack = (CompoundTag) in.readTag();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
