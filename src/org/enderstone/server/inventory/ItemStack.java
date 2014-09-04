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
package org.enderstone.server.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.enderstone.server.regions.BlockId;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

/**
 *
 * @author Fernando
 */
public class ItemStack implements Cloneable {

	private short blockId;
	private byte amount;
	private short damage;
	private CompoundTag compoundTag;

	public ItemStack(short blockId, byte amount, short damage) {
		this(blockId, amount, damage, null);
	}

	public ItemStack(short blockId, byte amount, short damage, CompoundTag compoundTag) {
		this.blockId = blockId;
		this.amount = amount;
		this.damage = damage;
		this.compoundTag = compoundTag;
		if (compoundTag == null) {
			this.updateNBTData();
		}
	}

	public void updateNBTData() {
		Map<String, Tag> map = new HashMap<>();
		map.put("Count", new ByteTag("Count", this.getAmount()));
		map.put("Damage", new ShortTag("Damage", this.getDamage()));
		map.put("id", new StringTag("id", "minecraft:grass"));
		this.compoundTag = new CompoundTag("Item", map);
		//this.compoundTag = null;
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

	public CompoundTag getCompoundTag() {
		return compoundTag;
	}

	public void setCompoundTag(CompoundTag compoundTag) {
		this.compoundTag = compoundTag;
	}
	
	@Override
	@SuppressWarnings("CloneDeclaresCloneNotSupported")
	public ItemStack clone() { //TODO Faster clone and what if compoundtag == null? 
		try {
			ItemStack s = (ItemStack) super.clone();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new NBTOutputStream(bytes).writeTag(compoundTag);
			s.setCompoundTag((CompoundTag)new NBTInputStream(new ByteArrayInputStream(bytes.toByteArray())).readTag());
			return s;
		} catch (CloneNotSupportedException | IOException err) {
			throw new AssertionError(err);
		}
	}
	
	public BlockId getId()
	{
		return BlockId.byId(this.blockId);
	}
	
	public boolean materialTypeMatches(ItemStack other)
	{
		return other.blockId == this.blockId; // TODO do this better (also compare item damages)
	}
}
