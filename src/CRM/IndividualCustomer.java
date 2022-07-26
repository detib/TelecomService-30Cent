package CRM;

import CRM.Contact.IndividualContact;
import CRM.Enum.Gender;
import CRM.Enum.STATE;
import Database.DatabaseConn;
import lombok.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IndividualCustomer extends Customer{
    @Setter
    private String name;
    @Setter
    private String lastname;
    @Setter
    private Long idNumber;
    @Setter
    private Gender gender;
    @Setter
    private LocalDate dob;
    private IndividualContact contact;

    public IndividualCustomer(String name, String lastname, Long idNumber,
                              Gender gender, LocalDate dob) {
        this.name = name;
        this.lastname = lastname;
        this.idNumber = idNumber;
        this.gender = gender;
        this.dob = dob;
    }

    @Override
    public boolean create() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().execute(
                    String.format("INSERT INTO IndividualCustomer VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                            getId(), name, lastname, idNumber, gender, dob, contact, getCreatedDate(), getState()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().execute(
                    String.format("UPDATE IndividualCustomer SET `name`='%s'," +
                                    "`surname`='%s',`gender`='%s',`dob`='%s'," +
                                    "`contact`='%s',`state`='%s' where CuId='%s';",
                            name, lastname, gender, dob, contact, getState(), getId()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public ResultSet findById() {
        return null;
    }

    @Override
    public ResultSet findAll() {
        return null;
    }

//    public static void main(String[] args) {
//        IndividualCustomer i = new IndividualCustomer("Blendi", "RRustemi", 1170609995L,
//                Gender.M, LocalDate.of(2001, 01, 13));
//        if(i.create()) {
//            System.out.println("Created");
//        } else {
//            System.out.println("Not Created");
//        }
//    }

    public static void main(String[] args) {
        new IndividualCustomer("aa", "bb", 123132L, Gender.F, LocalDate.of(2000,10,10));
        new IndividualCustomer("aa", "bb", 123132L, Gender.F, LocalDate.of(2000,10,10));
        new IndividualCustomer("aa", "bb", 123132L, Gender.F, LocalDate.of(2000,10,10));
        IndividualCustomer i = new IndividualCustomer("QEJNGsadasdJT", "MBIEMRIIGRUJES", 1235L, Gender.F,
                LocalDate.of(2001, 01, 13));
        i.create();
        i.setName("UNDRRUE");
        i.setGender(Gender.M);
        i.setState(STATE.DEACTIVE);
        i.update();
    }

}
