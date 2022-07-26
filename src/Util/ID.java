package Util;

public enum ID {
    CONTACT("CNT"),
    CUSTOMER("CUS"),
    CONTRACT("CRT"),
    SUBSCRIPTION("SUB"),
    SERVICE("SER"),
    SIMCARD("SIM"),
    SMS("SMS"),
    DATA("DAT"),
    VOICE("VCE"),
    PRODUCT("PRD");

    private final String prefix;


    private Long count;

    ID(String prefix) {
        this(prefix, 1000000L);
    }

    ID(String prefix, Long count) {
        this.prefix = prefix;
        this.count = count;
    }

    public String createId() {
        return String.format("%S_%d", prefix, ++count);
    }
}
