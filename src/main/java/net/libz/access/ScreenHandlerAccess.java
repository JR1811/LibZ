package net.libz.access;

import net.minecraft.util.math.BlockPos;

/**
 * Interface to set and get the BlockPos of a block entity in a ScreenHandler.
 */
public interface ScreenHandlerAccess {

    BlockPos getPos();

    void setPos(BlockPos pos);

}
