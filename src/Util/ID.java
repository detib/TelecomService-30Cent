package Util;

import Database.DatabaseConn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try {
            Connection con = DatabaseConn.getInstance().getConnection();
            ResultSet counter = con.createStatement().executeQuery(String.format("SELECT %s from counter;", prefix));
            int counterValue = 0;
            if (counter.next()) {
                counterValue = counter.getInt(prefix);
            }
            con.createStatement().executeUpdate(String.format("UPDATE counter SET %s=%s+1;", prefix, prefix));
            Long currentCounter = counterValue + count;
            return String.format("%S_%d", prefix, currentCounter);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
