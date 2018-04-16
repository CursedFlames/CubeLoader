package cursedflames.cubeloader.block.cubeloader;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.lib.block.GenericTileBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCubeLoader extends GenericTileBlock {
	public BlockCubeLoader() {
		super(CubeLoader.MODID, "cubeloader", TileCubeLoader.class, CubeLoader.TAB_CUBELOADER,
				Material.IRON, 3.0f, 100.0f);
		setLightLevel(5f/16f);
		CubeLoader.registryHelper.addBlock(this).addItemBlock(this).addItemBlockModel(this)
				.addTileEntity(getUnlocalizedName(), TileCubeLoader.class);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCubeLoader) {
			TileCubeLoader te = (TileCubeLoader) tile;
			te.owner = placer.getUniqueID();
			te.updateCubeLoading(false);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
			EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		if (world.isRemote) {
			return true;
		}
		// TODO gui Ids?
		player.openGui(CubeLoader.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
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

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
