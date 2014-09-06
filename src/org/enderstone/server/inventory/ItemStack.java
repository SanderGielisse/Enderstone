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
import java.util.Objects;
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

	private void updateNBTData() {
		Map<String, Tag> map = new HashMap<>();
		if (map.isEmpty()) return;
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
	
	public void setAmount(int amount) {
		if(amount > 256 || amount < 0) throw new IllegalArgumentException("Provided itemstack amount is to high! " + amount);
		this.amount = (byte) amount;
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
	public ItemStack clone() {
		try {
			ItemStack s = (ItemStack) super.clone();
			if (compoundTag == null) return s;
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new NBTOutputStream(bytes).writeTag(compoundTag);
			s.setCompoundTag((CompoundTag) new NBTInputStream(new ByteArrayInputStream(bytes.toByteArray())).readTag());
			return s;
		} catch (CloneNotSupportedException | IOException err) {
			throw new AssertionError(err);
		}
	}

	public BlockId getId() {
		return BlockId.byId(this.blockId);
	}

	public boolean materialTypeMatches(ItemStack other) {
		return other.blockId == this.blockId
				&& other.damage == this.damage
				&& (other.compoundTag == null ? this.compoundTag == null : other.compoundTag.equals(this.compoundTag));
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + this.blockId;
		hash = 23 * hash + this.amount;
		hash = 23 * hash + this.damage;
		hash = 23 * hash + Objects.hashCode(this.compoundTag);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ItemStack other = (ItemStack) obj;
		if (this.blockId != other.blockId) return false;
		if (this.amount != other.amount) return false;
		if (this.damage != other.damage) return false;
		return Objects.equals(this.compoundTag, other.compoundTag);
	}

	@Override
	public String toString() {
		return "ItemStack{" + "Id=" + blockId + ", amount=" + amount + ", data=" + damage + ", tag=" + compoundTag + '}';
	}
	
	
}
