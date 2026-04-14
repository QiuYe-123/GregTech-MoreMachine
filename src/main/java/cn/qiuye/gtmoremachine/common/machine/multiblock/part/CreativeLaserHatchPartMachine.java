package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.item.behavior.PortableScannerBehavior;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.utils.ISubscription;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.api.GTValues.MAX_PLUS_FORMAT;
import static net.minecraft.ChatFormatting.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreativeLaserHatchPartMachine extends TieredIOPartMachine implements IDataInfoProvider {

    @SaveField
    private final NotifiableLaserContainer buffer;
    @Nullable
    protected ISubscription LaserListener;
    protected TickableSubscription explosionSubs;
    private Long maxEnergy;
    @SaveField
    private long voltage;
    @SaveField
    @Getter
    private int amps = 1;
    @SaveField
    private int setTier = GTValues.VNF.length - 1;

    public static final String[] VNF = new String[] {
            BLUE + "IV",
            LIGHT_PURPLE + "LuV",
            RED + "ZPM",
            DARK_AQUA + "UV",
            DARK_RED + "UHV",
            GREEN + "UEV",
            DARK_GREEN + "UIV",
            YELLOW + "UXV",
            BLUE.toString() + BOLD + "OpV",
            RED.toString() + BOLD + "MAX",
            MAX_PLUS_FORMAT.apply(1),
            MAX_PLUS_FORMAT.apply(2),
            MAX_PLUS_FORMAT.apply(3),
            MAX_PLUS_FORMAT.apply(4),
            MAX_PLUS_FORMAT.apply(5),
            MAX_PLUS_FORMAT.apply(6),
            MAX_PLUS_FORMAT.apply(7),
            MAX_PLUS_FORMAT.apply(8),
            MAX_PLUS_FORMAT.apply(9),
            MAX_PLUS_FORMAT.apply(10),
            MAX_PLUS_FORMAT.apply(11),
            MAX_PLUS_FORMAT.apply(12),
            MAX_PLUS_FORMAT.apply(13),
            MAX_PLUS_FORMAT.apply(14),
            MAX_PLUS_FORMAT.apply(15),
            MAX_PLUS_FORMAT.apply(16), };

    public CreativeLaserHatchPartMachine(BlockEntityCreationInfo holder) {
        super(holder, GTValues.MAX, IO.IN);
        this.voltage = GTValues.VEX[this.setTier];
        this.maxEnergy = this.voltage * this.amps;
        this.buffer = this.attachTrait(NotifiableLaserContainer.receiverContainer(this.maxEnergy, this.voltage, this.amps));
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.LaserListener != null) {
            this.LaserListener.unsubscribe();
            this.LaserListener = null;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.LaserListener = this.buffer.addChangedListener(this::AddEngerySubscription);
        AddEngerySubscription();
    }

    protected void AddEngerySubscription() {
        this.explosionSubs = subscribeServerTick(this.explosionSubs, this::addEng);
    }

    protected void addEng() {
        if (this.buffer.getInputVoltage() != this.voltage || this.buffer.getInputAmperage() != this.amps) {
            this.maxEnergy = this.voltage * this.amps;
            this.buffer.resetBasicInfo(this.maxEnergy, this.voltage, this.amps, 0, 0);
            this.buffer.setEnergyStored(0);
        }
        if (this.buffer.getEnergyStored() < this.maxEnergy) {
            this.buffer.setEnergyStored(this.maxEnergy);
        }
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(176, 136, this, entityPlayer)
                .background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(7, 32, "gtceu.creative.energy.voltage"))
                .widget(new TextFieldWidget(9, 47, 152, 16, () -> String.valueOf(this.voltage),
                        value -> {
                            this.voltage = Long.parseLong(value);
                            this.setTier = GTUtil.getTierByVoltage(this.voltage);
                        }).setNumbersOnly(8192L, Long.MAX_VALUE))
                .widget(new LabelWidget(7, 74, "gtceu.creative.energy.amperage"))
                .widget(new ButtonWidget(7, 87, 20, 20,
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("-")),
                        cd -> this.amps = --this.amps == -1 ? 0 : this.amps))
                .widget(new TextFieldWidget(31, 89, 114, 16, () -> String.valueOf(this.amps),
                        value -> this.amps = Integer.parseInt(value)).setNumbersOnly(1, 67108864))
                .widget(new ButtonWidget(149, 87, 20, 20,
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("+")),
                        cd -> {
                            if (this.amps < Integer.MAX_VALUE) {
                                this.amps++;
                            }
                        }))

                .widget(new SelectorWidget(7, 7, 30, 20, Arrays.stream(VNF).toList(), -1)
                        .setOnChanged(tier -> {
                            this.setTier = ArrayUtils.indexOf(VNF, tier) + 5;
                            this.voltage = GTValues.VEX[this.setTier];
                        })
                        .setSupplier(() -> VNF[this.setTier - 5])
                        .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                        .setBackground(ColorPattern.BLACK.rectTexture())
                        .setValue(VNF[this.setTier - 5]));
    }

    @Override
    public List<Component> getDataInfo(PortableScannerBehavior.DisplayMode mode) {
        if (mode == PortableScannerBehavior.DisplayMode.SHOW_ALL || mode == PortableScannerBehavior.DisplayMode.SHOW_ELECTRICAL_INFO) {
            return Collections.singletonList(Component.literal(
                    String.format("%d/%d EU", this.buffer.getEnergyStored(), this.buffer.getEnergyCapacity())));
        }
        return new ArrayList<>();
    }
}
