package CRM.Contact;

import CRM.Enum.Gender;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class IndividualContact extends Contact implements TelecomService {
    private String name;
    private String lastname;
    private Gender gender;
    private LocalDate dob;

    public IndividualContact(String name, String lastname, Gender gender, LocalDate dob) {
        super(ID.CUSTOMER);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
    }

    @Override
    public boolean create() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().execute(
                    String.format("INSERT INTO IndividualContact VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                            getId(), name, lastname, gender, dob, getIdType(), getCreatedDate(), getState()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update() {
        return false;
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
}
