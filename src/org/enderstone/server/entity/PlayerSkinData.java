
package org.enderstone.server.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Fernando
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

