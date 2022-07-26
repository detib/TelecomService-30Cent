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
                              Gender gender, LocalDate dob, IndividualContact contact) {
        this.name = name;
        this.lastname = lastname;
        this.idNumber = idNumber;
        this.gender = gender;
        this.dob = dob;
        this.contact = contact;
    }

    @Override
    public boolean create() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().execute(
                    String.format("INSERT INTO IndividualCustomer VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                            getId(), name, lastname, idNumber, gender, dob, contact.getId(), getCreatedDate(), getState()));

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

    // LOOK AT IT LATER
    @Override
    public ResultSet findById() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().executeQuery(
                    String.format("Select * from IndividualCustomer where CuId='%s';", getId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet findAll() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().executeQuery(
                    String.format("Select * from IndividualCustomer"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
