package cn.qiuye.gtmoremachine.api.lang;

public record CNEN(String cn, String en) {

    public CNEN create(String cn, String en) {
        return new CNEN(cn, en);
    }
}
