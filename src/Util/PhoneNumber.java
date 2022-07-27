package Util;

import Database.DatabaseConn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum PhoneNumber {
    FOUR(38344, "four"),
    FIVE(38345, "five"),
    SIX(38346, "six");

    private final String number;
    private Integer prefix;
    private Long count;

    PhoneNumber(Integer prefix, String number) {
        this(prefix, 100000L, number);
    }

    PhoneNumber(Integer prefix, Long count, String number) {
        this.prefix = prefix;
        this.count = count;
        this.number = number;
    }

    public String generateNumber() {
        try {
            Connection con = DatabaseConn.getInstance().getConnection();
            ResultSet counter = con.createStatement().executeQuery(String.format("SELECT %s from counter;", number));
            int counterValue = 0;
            if (counter.next()) {
                counterValue = counter.getInt(number);
            }
            con.createStatement().executeUpdate(String.format("UPDATE counter SET %s=%s+1;", number, number));
            Long currentCounter = counterValue + count;
            return String.format("+%S%d", prefix, currentCounter);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
