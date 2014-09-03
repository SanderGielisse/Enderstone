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
package org.enderstone.server.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author ferrybig
 */
public class PlayerSkinData implements Serializable{
	private static final long serialVersionUID = 457334256L;
	
	public final String signature;
	public final String value;

	public PlayerSkinData(String signature, String value) {
		this.signature = signature;
		this.value = value;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.signature);
		hash = 29 * hash + Objects.hashCode(this.value);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PlayerSkinData other = (PlayerSkinData) obj;
		if (!Objects.equals(this.signature, other.signature)) return false;
		return Objects.equals(this.value, other.value);
	}

	@Override
	public String toString() {
		return "PlayerSkinData{" + "signature=" + signature + ", value=" + value + '}';
	}
	
	
}

