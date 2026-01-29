package cn.qiuye.gtmoremachine.api.lang;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.LogicalSide;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TraditionalChineseLanguageProvider extends LanguageProvider implements RegistrateProvider {

    public static final ProviderType<TraditionalChineseLanguageProvider> LANG = ProviderType.register("tw_lang", (registrate, vanillaPack) -> new TraditionalChineseLanguageProvider(registrate, vanillaPack.getGenerator().getPackOutput()));
    private final AbstractRegistrate<?> owner;

    private TraditionalChineseLanguageProvider(AbstractRegistrate<?> registrate, PackOutput vanillaPack) {
        super(vanillaPack, registrate.getModid(), "zh_tw");
        this.owner = registrate;
    }

    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    public String getName() {
        return "Lang (zh_tw)";
    }

    protected void addTranslations() {
        this.owner.genData(LANG, this);
    }
}
