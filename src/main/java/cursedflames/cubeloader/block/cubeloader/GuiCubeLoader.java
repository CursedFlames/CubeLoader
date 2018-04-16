package cursedflames.cubeloader.block.cubeloader;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.cubeloader.network.PacketHandler;
import cursedflames.lib.Util;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;

//TODO figure out how to do dynamic GUI sizes instead of making it height 200
//TODO add gui background
//TODO make the slider names based on locale files instead of hardcoded
public class GuiCubeLoader extends GuiContainer /* implements ISlider */ {
	public static final int WIDTH = 176;
	public static final int HEIGHT = 200;
	private TileCubeLoader te;

	private static final ResourceLocation background = new ResourceLocation(CubeLoader.MODID,
			"textures/gui/containercubeloader.png");

	public GuiCubeLoader(TileCubeLoader tileEntity, ContainerCubeLoader container) {
		super(container);
		te = tileEntity;

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	public void initGui() {
		super.initGui();
		// TODO config option for max horizontal/vertical range
		// TODO set slider default to current range
		// TODO maybe make sliders snap to values instead of going smoothly -
		// example of this is video settings mipmap slider
		buttonList.add(new GuiSlider(0, guiLeft+8, guiTop+4, 160, 16, "X Radius: ", "", 0, 5,
				te.getXRange(), false, true, null));
		buttonList.add(new GuiSlider(1, guiLeft+8, guiTop+22, 160, 16, "Y Radius: ", "", 0, 5,
				te.getYRange(), false, true, null));
		buttonList.add(new GuiSlider(2, guiLeft+8, guiTop+40, 160, 16, "Z Radius: ", "", 0, 5,
				te.getZRange(), false, true, null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	// @Override
	// public void onChangeSliderValue(GuiSlider slider) {
	//
	// }

	// TODO also update ranges when sliders move, so players in multiplayer can
	// see the range change if the range indicator is on - or add set button
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		PacketHandler.INSTANCE.sendToServer(new NBTPacket(getRangeConfigUpdatePacket()));
	}

	// TODO figure out how to send entities in packets, for cubeloader minecart
	private NBTTagCompound getRangeConfigUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("pos", Util.blockPosToNBT(((ContainerCubeLoader) inventorySlots).te.getPos()));
		tag.setInteger("xRange", ((GuiSlider) buttonList.get(0)).getValueInt());
		tag.setInteger("yRange", ((GuiSlider) buttonList.get(1)).getValueInt());
		tag.setInteger("zRange", ((GuiSlider) buttonList.get(2)).getValueInt());
		return tag;
	}
}
