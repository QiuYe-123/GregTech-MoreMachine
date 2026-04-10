package cn.qiuye.gtmoremachine.api.gui.widget;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.common.block.BlockMap;
import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

@GTMMDataGeneratorScanned
@SuppressWarnings("ConstantConditions")
public class PatternWidgetGroup extends WidgetGroup {

    private static final String valuePrefix = AdvancedTerminalBehavior.valuePrefix + "block";

    @GTMMRegisterLanguage(en = "Cancel", cn = "取消")
    private static final String cancel = valuePrefix + ".cancel";
    @GTMMRegisterLanguage(en = "Confirm", cn = "确认")
    private static final String confirm = valuePrefix + ".confirm";
    @GTMMRegisterLanguage(en = "Select Block", cn = "选择方块")
    private static final String select = valuePrefix + ".select";

    // ==================== 静态纹理按钮 ====================
    public static final GuiTextureGroup BUTTON_LEFT_NORMAL = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("◀"));
    public static final GuiTextureGroup BUTTON_RIGHT_NORMAL = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("▶"));
    public static final GuiTextureGroup BUTTON_LEFT_SELECTED = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("◀").setColor(ChatFormatting.AQUA.getColor()));
    public static final GuiTextureGroup BUTTON_RIGHT_SELECTED = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("▶").setColor(ChatFormatting.AQUA.getColor()));
    public static final GuiTextureGroup BUTTON_CHECK_NORMAL = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✔"));
    public static final GuiTextureGroup BUTTON_CROSS_NORMAL = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✘"));
    public static final GuiTextureGroup BUTTON_CHECK_SELECTED = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✔").setColor(ChatFormatting.GREEN.getColor()));
    public static final GuiTextureGroup BUTTON_CROSS_SELECTED = new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✘").setColor(ChatFormatting.GREEN.getColor()));

    // ==================== 静态缓存数据 ====================
    private static int CATEGORY_COUNT;
    private static int MAX_BLOCKS_PER_CATEGORY;
    private static boolean INITIALIZED = false;

    // ==================== 实例字段 ====================
    private final Widget leftPanel;          // 左侧类别选择面板
    private final Widget rightPanel;         // 右侧方块选择面板
    private final WidgetGroup categoryListPanel;  // 类别列表容器
    private final WidgetGroup blockListPanel;     // 方块列表容器
    private final WidgetGroup[] categoryRowWidgets;  // 每个类别对应的行组件
    private final BlockRowWidget[] blockRowWidgets;  // 每个方块对应的行组件
    private int selectedBlockIndex = 0;           // 当前选中的方块索引（1-based）
    private final ItemStack dataItemStack;        // 存储选中数据的物品
    private final BiConsumer<String, Integer> blockSelectionConsumer; // 保存选中结果的回调
    @Getter
    private boolean editingMode = false;          // 是否处于编辑模式
    private String selectedCategory = null;       // 当前选中的类别名称

    // ==================== 构造方法 ====================
    public PatternWidgetGroup(int x, int y, int width, int height, BooleanConsumer onEditingModeChanged, ItemStack dataStack) {
        super(x, y, width, height);

        // 初始化静态数据（仅一次）
        if (!INITIALIZED) {
            CATEGORY_COUNT = BlockMap.MAP.size();
            MAX_BLOCKS_PER_CATEGORY = BlockMap.MAP.values().stream().mapToInt(blocks -> blocks.length).max().orElse(0);
            INITIALIZED = true;
        }

        this.categoryRowWidgets = new WidgetGroup[CATEGORY_COUNT];
        this.blockRowWidgets = new BlockRowWidget[MAX_BLOCKS_PER_CATEGORY];
        this.dataItemStack = dataStack;
        this.blockSelectionConsumer = (category, index) -> {
            if (category != null && index != 0) {
                CompoundTag rootTag = dataStack.getOrCreateTag();
                CompoundTag blocksTag = rootTag.getCompound("blocks");
                if (blocksTag.contains(category) && blocksTag.getInt(category) == index) {
                    blocksTag.remove(category);
                } else {
                    blocksTag.putInt(category, index);
                }
                rootTag.put("blocks", blocksTag);
                dataStack.setTag(rootTag);
            }
        };

        // 创建左侧滚动面板
        this.categoryListPanel = new WidgetGroup();
        DraggableScrollableWidgetGroup leftScroll = new DraggableScrollableWidgetGroup(0, 4, 45, 172)
                .setYScrollBarWidth(2)
                .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1.0F));
        leftScroll.addWidget(this.categoryListPanel);
        this.leftPanel = new WidgetGroup(width + 19, -8, 50, 180)
                .addWidget(leftScroll)
                .setBackground(GuiTextures.BACKGROUND_INVERSE)
                .setActive(false)
                .setVisible(false);
        // 创建右侧滚动面板
        this.blockListPanel = new WidgetGroup();
        DraggableScrollableWidgetGroup rightScroll = new DraggableScrollableWidgetGroup(0, 4, 45, 172)
                .setYScrollBarWidth(2)
                .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1.0F));
        rightScroll.addWidget(this.blockListPanel);
        this.rightPanel = new WidgetGroup(width + 23 + 50, -8, 50, 180)
                .addWidget(rightScroll)
                .setBackground(GuiTextures.BACKGROUND_INVERSE)
                .setActive(false)
                .setVisible(false);

        this.addWidget(this.leftPanel);
        this.addWidget(this.rightPanel);

        // 编辑模式切换按钮（铅笔/叉）
        ToggleButtonWidget editToggle = new ToggleButtonWidget(this.getSizeWidth() - this.getSizeHeight(), 0, this.getSizeHeight(), this.getSizeHeight(),
                this::isEditingMode,
                enabled -> {
                    this.editingMode = enabled;
                    if (enabled) {
                        this.leftPanel.setActive(true);
                        this.leftPanel.setVisible(true);
                        this.initCategoryList();
                    } else {
                        this.closeEditingMode();
                    }
                    onEditingModeChanged.accept(enabled);
                });
        editToggle.setTexture(
                new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✎")),
                new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture("✘")));
        editToggle.setHoverTooltips(Component.translatable(select));
        this.addWidget(editToggle);
    }

    // ==================== 公共方法 ====================
    public void closeEditingMode() {
        this.editingMode = false;
        this.selectedCategory = null;
        this.leftPanel.setActive(false);
        this.leftPanel.setVisible(false);
        this.rightPanel.setActive(false);
        this.rightPanel.setVisible(false);
    }

    // ==================== 私有辅助方法 ====================
    private boolean isBlockSelected(String category, Block block) {
        CompoundTag rootTag = this.dataItemStack.getOrCreateTag();
        CompoundTag blocksTag = rootTag.getCompound("blocks");
        int selectedIndex = blocksTag.getInt(category);
        Block[] blocks = BlockMap.MAP.get(category);
        return blocks != null && selectedIndex - 1 >= 0 && selectedIndex - 1 < blocks.length && blocks[selectedIndex - 1] == block;
    }

    private void initCategoryList() {
        AtomicInteger rowCounter = new AtomicInteger(0);
        for (Map.Entry<String, Block[]> entry : BlockMap.MAP.entrySet()) {
            String category = entry.getKey();
            BooleanSupplier isSelected = () -> this.dataItemStack.getOrCreateTag().getCompound("blocks").contains(category);

            if (this.categoryRowWidgets[rowCounter.get()] != null) {
                rowCounter.getAndIncrement();
                continue;
            }

            // 创建类别行组件
            WidgetGroup row = new WidgetGroup(0, 0, this.getSizeWidth() - 8, 18);
            this.categoryRowWidgets[rowCounter.get()] = row;
            row.setVisible(true);
            row.setActive(true);

            // 图标
            Block firstBlock = entry.getValue()[0];
            ItemIconWidget icon = new ItemIconWidget(firstBlock.asItem().getDefaultInstance(), 4, 18 * rowCounter.get(), null);
            row.addWidget(icon);

            // 切换按钮
            ToggleButtonWidget toggle = new ToggleButtonWidget(30, 2 + 18 * rowCounter.getAndIncrement(), 12, 12,
                    () -> category.equals(this.selectedCategory),
                    pressed -> {});
            toggle.setTexture(isSelected.getAsBoolean() ? BUTTON_LEFT_SELECTED : BUTTON_LEFT_NORMAL,
                    isSelected.getAsBoolean() ? BUTTON_RIGHT_SELECTED : BUTTON_RIGHT_NORMAL);
            toggle.setHoverTooltips(Component.translatable(select));

            BooleanConsumer onToggle = enabled -> {
                if (enabled) {
                    this.selectedCategory = category;
                    this.rightPanel.setActive(true);
                    this.rightPanel.setVisible(true);
                    this.showBlocksForCategory(category, () -> toggle.setTexture(isSelected.getAsBoolean() ? BUTTON_LEFT_SELECTED : BUTTON_LEFT_NORMAL,
                            isSelected.getAsBoolean() ? BUTTON_RIGHT_SELECTED : BUTTON_RIGHT_NORMAL));
                } else {
                    if (category.equals(this.selectedCategory)) {
                        this.selectedCategory = null;
                    }
                    this.rightPanel.setActive(false);
                    this.rightPanel.setVisible(false);
                }
            };
            toggle.setOnPressCallback((btn, pressed) -> onToggle.accept(pressed));
            row.addWidget(toggle);
            this.categoryListPanel.addWidget(row);
        }

        this.categoryListPanel.setSize(this.getSizeWidth() - 8, rowCounter.get() * 18 + 20);
    }

    private void showBlocksForCategory(String category, Runnable onUpdate) {
        // 清除旧的方块行
        for (BlockRowWidget row : this.blockRowWidgets) {
            if (row != null) {
                this.blockListPanel.removeWidget(row);
            }
        }

        Block[] blocks = BlockMap.MAP.getOrDefault(category, new Block[0]);
        int rowIndex = 0;
        for (Block block : blocks) {
            BlockRowWidget row;
            if (this.blockRowWidgets[rowIndex] != null) {
                row = this.blockRowWidgets[rowIndex];
            } else {
                row = new BlockRowWidget();
                this.blockRowWidgets[rowIndex] = row;
            }
            this.blockListPanel.addWidget(row);

            row.blockIndex = rowIndex + 1;
            row.itemIcon.setItemStack(block.asItem().getDefaultInstance());
            row.itemIcon.setSelfPosition(5, 18 * rowIndex);
            row.toggleButton.setSelfPosition(30, 2 + 18 * rowIndex);

            boolean selected = this.isBlockSelected(category, block);
            row.toggleButton.setTexture(selected ? BUTTON_CHECK_SELECTED : BUTTON_CHECK_NORMAL,
                    selected ? BUTTON_CROSS_SELECTED : BUTTON_CROSS_NORMAL);
            row.toggleButton.setSupplier(() -> this.isBlockSelected(category, block));
            row.toggleButton.setOnPressCallback((btn, pressed) -> {
                this.selectedBlockIndex = row.blockIndex;
                this.blockSelectionConsumer.accept(category, this.selectedBlockIndex);
                onUpdate.run();
                this.showBlocksForCategory(category, onUpdate);
            });

            if (selected) {
                row.toggleButton.setHoverTooltips(Component.translatable(cancel));
            } else {
                row.toggleButton.setHoverTooltips(Component.translatable(confirm));
            }

            rowIndex++;
        }

        this.blockListPanel.setSize(this.getSizeWidth() - 8, rowIndex * 18 + 20);
    }

    // ==================== 内部类：物品图标组件 ====================
    private static class ItemIconWidget extends Widget {

        @Setter
        private ItemStack itemStack;

        public ItemIconWidget(ItemStack stack, int x, int y, @Nullable String tooltip) {
            super(x, y, 16, 16);
            this.itemStack = stack;
            if (tooltip != null) {
                this.setHoverTooltips(Component.literal(tooltip));
            }
        }

        @Override
        public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
            Position pos = this.getPosition();
            DrawerHelper.drawItemStack(graphics, this.itemStack, pos.x, pos.y, -1, null);
        }

        @Override
        public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
            this.setHoverTooltips(Screen.getTooltipFromItem(Minecraft.getInstance(), this.itemStack));
        }
    }

    // ==================== 内部类：方块行组件（图标 + 确认按钮） ====================
    private static class BlockRowWidget extends WidgetGroup {

        private final ItemIconWidget itemIcon;
        private final ToggleButtonWidget toggleButton;
        private int blockIndex;

        public BlockRowWidget() {
            super(0, 0, 100, 20);
            this.itemIcon = new ItemIconWidget(ItemStack.EMPTY, 5, 5, null);
            this.toggleButton = new ToggleButtonWidget(20, 7, 12, 12, () -> false, pressed -> {});
            this.toggleButton.setTexture(BUTTON_CHECK_NORMAL, BUTTON_CROSS_NORMAL);
            this.addWidget(this.itemIcon);
            this.addWidget(this.toggleButton);
        }
    }
}
