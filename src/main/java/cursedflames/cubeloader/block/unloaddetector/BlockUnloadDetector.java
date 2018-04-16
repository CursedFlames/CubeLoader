package cursedflames.cubeloader.block.unloaddetector;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.lib.block.GenericTileBlock;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUnloadDetector extends GenericTileBlock {
	public static final PropertyInteger counter = PropertyInteger.create("counter", 0, 15);

	public BlockUnloadDetector() {
		super(CubeLoader.MODID, "unloaddetector", TileUnloadDetector.class,
				CubeLoader.TAB_CUBELOADER);
		CubeLoader.registryHelper.addBlock(this).addItemBlock(this).addItemBlockModel(this)
				.addTileEntity(getUnlocalizedName(), TileUnloadDetector.class);
		setDefaultState(blockState.getBaseState().withProperty(counter, 0));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
			EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileUnloadDetector) {
			((TileUnloadDetector) te).counter = 0;
		}
		return true;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, counter);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = safelyGetTileEntity(worldIn, pos);
		if (te instanceof TileUnloadDetector) {
			state = state.withProperty(counter, ((TileUnloadDetector) te).counter);
		}
		return state;
	}

	// Apparently you have to override these if you have states even if they're
	// states that don't need to be saved
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	// TODO figure out why compiler needs this to be here
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		try {
			return tileEntity.newInstance();
		} catch (InstantiationException|IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
