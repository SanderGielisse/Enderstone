package me.bigteddy98.mcserver;

public class Location {

	private String worldName;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public Location() {
	}

	public Location(String worldName, double x, double y, double z, float yaw, float pitch) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public int getBlockX() {
		return (int) this.getX();
	}

	public int getBlockZ() {
		return (int) this.getZ();
	}

	public int getBlockY() {
		return (int) this.getY();
	}

	public boolean isInRange(int viewDistance, Location otherLoc) {
		return Math.max(this.x < otherLoc.x ? otherLoc.x - this.x : this.x - otherLoc.x, this.z < otherLoc.z ? otherLoc.z - this.z : this.z - otherLoc.z) < viewDistance;
	}
}
