package cursedflames.cubeloader.chunkloading;

import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import cursedflames.lib.block.DimensionBlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ChunkloaderPos extends DimensionBlockPos {

	public ChunkloaderPos() {
		super();
	}

	public ChunkloaderPos(BlockPos pos, int dim) {
		super(pos, dim);
	}

	public ChunkloaderPos(double x, double y, double z, int dim) {
		super(x, y, z, dim);
	}

	public ChunkloaderPos(int x, int y, int z, int dim) {
		super(x, y, z, dim);
	}

	public ChunkloaderPos(DimensionBlockPos dimPos) {
		super(dimPos.getPos(), dimPos.getDim());
	}

	public TileCubeLoader getCubeLoader(boolean loadDim, boolean loadCube) {
		TileEntity te = getTileEntity(loadDim, loadCube);
		return te instanceof TileCubeLoader ? (TileCubeLoader) te : null;
	}
}
