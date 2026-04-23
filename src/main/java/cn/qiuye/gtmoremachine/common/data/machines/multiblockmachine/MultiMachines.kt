package cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine

object MultiMachines {

	@JvmStatic
	fun init() {
		WirelessMultiMachines.init()
		GTMMMultiblockMachineA.init()
	}
}
