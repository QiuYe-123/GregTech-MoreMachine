package cn.qiuye.gtmoremachine.api.misc.wireless.energy.record

data class LossEnergy(var wirelessEnergy: Long, var cabinEnergy: Long) {

	fun getAfterEnergy(): LossEnergy {
		wirelessEnergy = cabinEnergy.also { cabinEnergy = wirelessEnergy }
		return this
	}
}
