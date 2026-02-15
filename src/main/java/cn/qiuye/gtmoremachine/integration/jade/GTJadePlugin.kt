package cn.qiuye.gtmoremachine.integration.jade

import cn.qiuye.gtmoremachine.integration.jade.provider.WirelessEnergyProvider
import cn.qiuye.gtmoremachine.integration.jade.provider.WirelessOpticalComputationHatchProvider
import cn.qiuye.gtmoremachine.integration.jade.provider.WrelessCWUProvider

import com.gregtechceu.gtceu.api.block.MetaMachineBlock
import com.gregtechceu.gtceu.api.machine.MetaMachine

import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@Suppress("unused")
@WailaPlugin
class GTJadePlugin : IWailaPlugin {

    override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(WirelessEnergyProvider(), MetaMachine::class.java)
        registration.registerBlockDataProvider(WrelessCWUProvider(), MetaMachine::class.java)
        registration.registerBlockDataProvider(
            WirelessOpticalComputationHatchProvider(),
            MetaMachine::class.java,
        )
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(WirelessEnergyProvider(), MetaMachineBlock::class.java)
        registration.registerBlockComponent(WrelessCWUProvider(), MetaMachineBlock::class.java)
        registration.registerBlockComponent(WirelessOpticalComputationHatchProvider(), MetaMachineBlock::class.java)
    }
}
