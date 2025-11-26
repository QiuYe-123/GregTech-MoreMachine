package cn.qiuye.gtmoremachine.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class ItemLang {

    public static void init(RegistrateLangProvider provider) {
        provider.add("item.gtmoremachine.wireless_energy_receive_cover.tooltip.1", "§bPull Energy§7 from EU network to the machine as §fCover§7.");
        provider.add("item.gtmoremachine.wireless_energy_receive_cover.tooltip.2", "§7Can only used for §esingle block machine§7.Can't put on the machine blow the cover's voltage");
        provider.add("item.gtmoremachine.wireless_energy_receive_cover.tooltip.3", "§bEnergy transfer speed: §f%s §7EU/t");
        provider.add("item.gtmoremachine.wireless_transfer.tooltip.1", "§7Bind to: §f%s (%s)");
        provider.add("item.gtmoremachine.wireless_transfer.tooltip.2", "§7Right click the container with shift to bind container.Right click the air with shift to unbind.");
        provider.add("item.gtmoremachine.wireless_transfer.tooltip.bind.1", "Success bind to: %s (%s)");
        provider.add("item.gtmoremachine.wireless_transfer.tooltip.bind.2", "Success unbind.");
    }
}
