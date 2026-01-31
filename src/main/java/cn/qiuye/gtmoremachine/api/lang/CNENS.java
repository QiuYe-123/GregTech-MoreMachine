package cn.qiuye.gtmoremachine.api.lang;

public record CNENS(String[] cns, String[] ens) {

    public int length() {
        return ens.length;
    }
}
