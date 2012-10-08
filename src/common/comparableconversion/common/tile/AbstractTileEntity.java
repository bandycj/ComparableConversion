/**
 * 
 */
package comparableconversion.common.tile;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 3, 2012
 */

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class AbstractTileEntity extends TileEntity {
	public static final String OWNER_NBT_TAG_LABEL = "owner";
	public static final String STATE_NBT_TAG_LABEL = "state";
	public static final String DIRECTION_NBT_TAG_LABEL = "direction";
    
	private byte direction;
	private short state;
	private String owner;

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return owner.equals(player.username);
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		direction = nbtTagCompound.getByte(DIRECTION_NBT_TAG_LABEL);
		state = nbtTagCompound.getShort(STATE_NBT_TAG_LABEL);
		owner = nbtTagCompound.getString(OWNER_NBT_TAG_LABEL);
	}

	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		nbtTagCompound.setByte(DIRECTION_NBT_TAG_LABEL, direction);
		nbtTagCompound.setShort(STATE_NBT_TAG_LABEL, state);
		if (owner != null && owner != "") {
			nbtTagCompound.setString(OWNER_NBT_TAG_LABEL, owner);
		}
	}

}
