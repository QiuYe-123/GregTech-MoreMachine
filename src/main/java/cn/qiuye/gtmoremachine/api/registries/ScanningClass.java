package cn.qiuye.gtmoremachine.api.registries;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.GTMMScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.lang.CNEN;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScanningClass {

    public static final Map<String, CNEN> LANG = GTmm.isDataGen() ? new HashMap<>() : null;

    public static void init() {}

    static {
        long time = System.currentTimeMillis();
        Type scannedclass = Type.getType(GTMMScanned.class);
        Type datageneratorscanned = LANG == null ? null : Type.getType(GTMMDataGeneratorScanned.class);
        Set<String> loadedClasses = new HashSet<>();

        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotationData : scanData.getAnnotations()) {
                Type type = annotationData.annotationType();
                if (scannedclass.equals(type) || datageneratorscanned != null && datageneratorscanned.equals(type)) {
                    try {
                        String className = annotationData.memberName();
                        if (!loadedClasses.add(className)) continue;
                        Class<?> data = Class.forName(className);
                        if (LANG == null) continue;
                        for (Field field : data.getDeclaredFields()) {
                            if (field.isAnnotationPresent(GTMMRegisterLanguage.class)) {
                                GTMMRegisterLanguage registerLanguage = field.getAnnotation(GTMMRegisterLanguage.class);
                                try {
                                    String key = registerLanguage.key();
                                    if (key.isEmpty()) {
                                        String namePrefix = registerLanguage.namePrefix();
                                        if (!namePrefix.isEmpty()) {
                                            key = namePrefix + '.' + field.getName();
                                        } else {
                                            field.setAccessible(true);
                                            key = field.get((Object) null).toString();
                                            String valuePrefix = registerLanguage.valuePrefix();
                                            if (!valuePrefix.isEmpty()) {
                                                key = valuePrefix + '.' + key;
                                            }
                                        }
                                    }
                                    LANG.put(key, new CNEN(registerLanguage.cn(), registerLanguage.en()));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        GTmm.LOGGER.info("ScanningClass init time: {}ms", System.currentTimeMillis() - time);
    }
}
