package cn.qiuye.gtmoremachine.common.data

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.common.data.machines.CustomMachines
import cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration

import com.gregtechceu.gtceu.api.GTCEuAPI
import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs
import com.gregtechceu.gtceu.common.data.GTMachines

import net.minecraft.world.item.CreativeModeTab

import com.tterrag.registrate.util.entry.RegistryEntry

object GTMMCreativeModeTabs {
	@JvmField
	val CREATIVE_TAB: RegistryEntry<CreativeModeTab, CreativeModeTab> = GTMMRegistration.GTMM
		.defaultCreativeTab("creative") { builder: CreativeModeTab.Builder ->
			builder
				.displayItems(
					GTCreativeModeTabs.RegistrateDisplayItemsGenerator(
						"creative",
						GTMMRegistration.GTMM,
					),
				)
				.title(GTMMRegistration.GTMM.addLang("itemGroup", GTmm.id("creative"), "Creative Things"))
				.icon { GTMachines.CREATIVE_ENERGY.asStack() }
				.build()
		}
		.register()

	@JvmField
	val WIRELESS_TAB: RegistryEntry<CreativeModeTab, CreativeModeTab> = GTMMRegistration.GTMM
		.defaultCreativeTab("wireless") { builder: CreativeModeTab.Builder ->
			builder
				.displayItems(
					GTCreativeModeTabs.RegistrateDisplayItemsGenerator(
						"wireless",
						GTMMRegistration.GTMM,
					),
				)
				.title(GTMMRegistration.GTMM.addLang("itemGroup", GTmm.id("wireless"), "Gregtech Wireless"))
				.icon {
					WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH[
						if (GTCEuAPI.isHighTier()) {
							GTValues.MAX
						} else {
							GTValues.UHV -
								1
						},
					].asStack()
				}
				.build()
		}
		.register()

	@JvmField
	val MORE_MACHINES: RegistryEntry<CreativeModeTab, CreativeModeTab> = GTMMRegistration.GTMM
		.defaultCreativeTab("more_machines") { builder: CreativeModeTab.Builder ->
			builder
				.displayItems(
					GTCreativeModeTabs.RegistrateDisplayItemsGenerator(
						"more_machines",
						GTMMRegistration.GTMM,
					),
				)
				.title(GTMMRegistration.GTMM.addLang("itemGroup", GTmm.id("more_machines"), "More Machines"))
				.icon {
					CustomMachines.HUGE_INPUT_DUAL_HATCH[
						if (GTCEuAPI.isHighTier()) {
							GTValues.MAX
						} else {
							GTValues.UHV -
								1
						},
					].asStack()
				}
				.build()
		}
		.register()

	@JvmStatic
	fun init() {}
}
