package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.common.block.BlockMap;

import com.gregtechceu.gtceu.common.data.GTMachines;

public class GTMMBlocks {

    public static void init() {
        BlockMap.init();
        int j = 0;
        for (int i = 3; i <= 13; i++) {
            BlockMap.rotMap.put(j, GTMachines.ROTOR_HOLDER[i]);
            j++;
        }
    }
}
