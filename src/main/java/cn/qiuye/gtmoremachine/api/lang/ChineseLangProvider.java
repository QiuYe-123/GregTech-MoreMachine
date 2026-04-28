package cn.qiuye.gtmoremachine.api.lang;

import cn.qiuye.gtmoremachine.utils.datagen.ChineseConverter;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;

import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
public class ChineseLangProvider extends LanguageProvider implements RegistrateProvider {

    public static final ProviderType<ChineseLangProvider> LANG = ProviderType.register("cn_lang", (registrate, vanillaPack) -> new ChineseLangProvider(registrate, vanillaPack.getGenerator().getPackOutput()));

    private static class TraditionalChineseLangProvider extends LanguageProvider {

        public TraditionalChineseLangProvider(PackOutput output, String modid, String locale) {
            super(output, modid, locale);
        }

        @Override
        public void add(String key, String value) {
            super.add(key, value);
        }

        @Override
        protected void addTranslations() {}
    }

    private final AbstractRegistrate<?> owner;

    private final TraditionalChineseLangProvider traditional;

    private ChineseLangProvider(AbstractRegistrate<?> owner, PackOutput packOutput) {
        super(packOutput, owner.getModid(), "zh_cn");
        this.owner = owner;
        this.traditional = new TraditionalChineseLangProvider(packOutput, this.owner.getModid(), "zh_tw");
    }

    public String getName() {
        return "Lang (zh_cn/zh_tw)";
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }

    @Override
    protected void addTranslations() {
        this.owner.genData(LANG, this);
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        traditional.add(key, ChineseConverter.convert(value));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(super.run(cache), traditional.run(cache));
    }
}
