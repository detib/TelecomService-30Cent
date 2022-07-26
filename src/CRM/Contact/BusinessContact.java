package CRM.Contact;

import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
public abstract class BusinessContact extends Contact implements TelecomService {

    private String customerName;
    private String businessId;

    public BusinessContact(ID idType, String customerName, String businessId) {
        super(ID.CUSTOMER);
        this.customerName = customerName;
        this.businessId = businessId;
    }

    @Override
    public boolean create() {
        Connection con = null;
        try {
            con = DatabaseConn.getInstance().getConnection();
            return con.createStatement().execute(
                    String.format("INSERT INTO BusinessContact VALUES('%s', '%s', '%s', '%s', '%s', '%s');",
                            getId(), getIdType(), getCreatedDate(), getState(), customerName, businessId));

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
