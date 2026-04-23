package cn.qiuye.gtmoremachine.api.addon

import net.minecraftforge.fml.ModList
import net.minecraftforge.forgespi.language.ModFileScanData

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.objectweb.asm.Type

import java.lang.reflect.Constructor

object AddonFinder {
	private val LOGGER: Logger = LogManager.getLogger()
	internal var cache: MutableList<IGTMMAddon>? = null
	internal var modIdMap = HashMap<String, IGTMMAddon>()
	internal var modTierMap = HashMap<Int, IGTMMAddon>()

	val addons: MutableList<IGTMMAddon>?
		get() {
			if (cache == null) {
				cache = getInstances(GTMMAddon::class.java, IGTMMAddon::class.java)
				for (addon in cache!!) {
					modIdMap[addon.addonModId()] = addon
				}
				for (addon in modIdMap) {
					modTierMap[addon.value.addonTier()] = addon.value
				}
			}

			return cache
		}

	fun getAddon(modId: String): IGTMMAddon? = modIdMap[modId]

	private fun <T> getInstances(annotationClass: Class<*>, instanceClass: Class<T>): MutableList<T> {
		val annotationType = Type.getType(annotationClass)
		val allScanData = ModList.get().getAllScanData()
		val pluginClassNames = LinkedHashSet<String>()
		for (scanData in allScanData) {
			val annotations: Iterable<ModFileScanData.AnnotationData> = scanData.annotations
			for (a in annotations) {
				if (a.annotationType() == annotationType) {
					val memberName = a.memberName()
					pluginClassNames.add(memberName)
				}
			}
		}
		val instances = ArrayList<T>()
		for (className in pluginClassNames) {
			try {
				val asmClass = Class.forName(className)
				val asmInstanceClass = asmClass.asSubclass(instanceClass)
				val constructor: Constructor<out T> = asmInstanceClass.getDeclaredConstructor()
				val instance: T = constructor.newInstance()
				instances.add(instance)
			} catch (e: ReflectiveOperationException) {
				LOGGER.error("Failed to load: {}", className, e)
			} catch (e: LinkageError) {
				LOGGER.error("Failed to load: {}", className, e)
			}
		}
		return instances
	}
}
