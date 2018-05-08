package cursedflames.cubeloader.block.cubeloader;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.cubeloader.config.Config;
import cursedflames.cubeloader.network.PacketHandler;
import cursedflames.cubeloader.network.PacketHandler.HandlerIds;
import cursedflames.lib.Util;
import cursedflames.lib.gui.GuiBetterButton;
import cursedflames.lib.gui.GuiSlider;
import cursedflames.lib.gui.IGuiSliderListener;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

//TODO figure out how to do dynamic GUI sizes instead of making it height 200
//TODO add gui background
//TODO make the slider names based on locale files instead of hardcoded
public class GuiCubeLoader extends GuiContainer implements IGuiSliderListener {
	public static final int WIDTH = 176;
	public static final int HEIGHT = 256;
	private TileCubeLoader te;
	private ContainerCubeLoader cont;

	private static final ResourceLocation background = new ResourceLocation(CubeLoader.MODID,
			"textures/gui/containercubeloader.png");

	public GuiCubeLoader(TileCubeLoader tileEntity, ContainerCubeLoader container) {
		super(container);
		te = tileEntity;
		cont = container;

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	public void initGui() {
		super.initGui();
		// TODO config option for max horizontal/vertical range
		// TODO maybe make sliders snap to values instead of going smoothly -
		// example of this is video settings mipmap slider
		GuiSlider sliderX = new GuiSlider(0, guiLeft+8, guiTop+47, 160, 12, "X Radius: ", 0, 5,
				te.getXRange(), false, true, true, this);
		GuiSlider sliderY = new GuiSlider(1, guiLeft+8, guiTop+60, 160, 12, "Y Radius: ", 0, 5,
				te.getYRange(), false, true, true, this);
		GuiSlider sliderZ = new GuiSlider(2, guiLeft+8, guiTop+73, 160, 12, "Z Radius: ", 0, 5,
				te.getZRange(), false, true, true, this);
		buttonList.add(sliderX);
		buttonList.add(sliderY);
		buttonList.add(sliderZ);
		GuiBetterButton disableButton = new GuiBetterButton(3, guiLeft+4,
				guiTop+fontRenderer.FONT_HEIGHT*3+7, 50, 12, "Disable");
		buttonList.add(disableButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1, 1);
		this.drawDefaultBackground();
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	private static final int GREEN = 0xFF00CC00, YELLOW = 0xFFCC8800, RED = 0xFFCC0000, BLACK = 0;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft+4, guiTop+4, 0);
		GlStateManager.disableLighting();
		boolean disabled = cont.disabled;
		buttonList.get(3).displayString = disabled ? "Enable" : "Disable";
		boolean fueled = cont.fueled;
		boolean paused = cont.paused;
		int teInRange = cont.cubesLoaded;
		int globalLoaded = cont.globalLoaded;
		int maxLoaded = Config.SyncedConfig.INSTANCE!=null
				? Config.SyncedConfig.INSTANCE.maxCubesLoaded : 256;
		String loadFraction = ""+globalLoaded+(maxLoaded==-1 ? "" : ("/"+maxLoaded));
		// TODO stop hardcoding text
		if (disabled) {
			fontRenderer.drawString("Disabled", 0, 0, RED, false);
		} else if (!fueled) {
			fontRenderer.drawString("Out of Fuel", 0, 0, YELLOW, false);
		} else {
			fontRenderer.drawString("Enabled", 0, 0, GREEN, false);
			fontRenderer.drawString(teInRange+" cube"+(teInRange==1 ? "" : "s")+" loaded", 0,
					fontRenderer.FONT_HEIGHT+1, GREEN, false);
		}
		fontRenderer.drawString(loadFraction, WIDTH-8-fontRenderer.getStringWidth(loadFraction), 0,
				(fueled&&(teInRange+globalLoaded<=maxLoaded||!disabled)) ? GREEN : YELLOW);
		String owner = cont.ownerName==null||cont.ownerName.length()==0 ? "Unknown"
				: cont.ownerName;
		fontRenderer.drawString("Owner: "+owner, 0, fontRenderer.FONT_HEIGHT*2+2, BLACK);
		if (teInRange+globalLoaded>maxLoaded&&disabled) {
			fontRenderer.drawString("Can't enable, over cap", 52, fontRenderer.FONT_HEIGHT*3+5,
					YELLOW);
			buttonList.get(3).enabled = false;
		} else {
			buttonList.get(3).enabled = true;
		}
		GlStateManager.popMatrix();
	}

	private void sendUpdatePacket(NBTTagCompound tag) {
		tag.setTag("pos", Util.blockPosToNBT(((ContainerCubeLoader) inventorySlots).te.getPos()));
		PacketHandler.INSTANCE
				.sendToServer(new NBTPacket(tag, HandlerIds.UPDATE_CUBELOADER_CONFIG.id));
	}

	@Override
	public void sliderActionPerformed(GuiButton slider) {
		// TODO make sure the int actually changes before sending
		sendUpdatePacket(getRangeConfigUpdatePacket());
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id==3) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("disabled", !cont.disabled);
			sendUpdatePacket(tag);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		PacketHandler.INSTANCE.sendToServer(new NBTPacket(getRangeConfigUpdatePacket(),
				HandlerIds.UPDATE_CUBELOADER_CONFIG.id));
	}

	// TODO figure out how to send entities in packets, for CL minecart - UUID?
	private NBTTagCompound getRangeConfigUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("xRange", ((GuiSlider) buttonList.get(0)).getSliderValueInt());
		tag.setInteger("yRange", ((GuiSlider) buttonList.get(1)).getSliderValueInt());
		tag.setInteger("zRange", ((GuiSlider) buttonList.get(2)).getSliderValueInt());
		return tag;
	}

}
