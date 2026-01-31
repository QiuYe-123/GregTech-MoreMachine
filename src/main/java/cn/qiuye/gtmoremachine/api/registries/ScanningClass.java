package cn.qiuye.gtmoremachine.api.registries;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.GTMMScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.lang.CNEN;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScanningClass {

    public static final Map<String, CNEN> LANG = GTmm.isDataGen() ? new HashMap<>() : null;

    public static void init() {}

    static {
        long time = System.currentTimeMillis();
        Type scannedclass = Type.getType(GTMMScanned.class);
        Type datageneratorscanned = LANG == null ? null : Type.getType(GTMMDataGeneratorScanned.class);

        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotationData : scanData.getAnnotations()) {
                Type type = annotationData.annotationType();
                if (Objects.equals(type, scannedclass) || datageneratorscanned != null && Objects.equals(type, datageneratorscanned)) {
                    try {
                        Class<?> data = Class.forName(annotationData.memberName());
                        for (Field field : data.getDeclaredFields()) {
                            if (LANG != null && field.isAnnotationPresent(GTMMRegisterLanguage.class)) {
                                GTMMRegisterLanguage registerLanguage = field.getAnnotation(GTMMRegisterLanguage.class);
                                try {
                                    assert registerLanguage != null;
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
