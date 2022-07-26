package CRM;

import CRM.Contact.IndividualContact;
import CRM.Enum.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString(callSuper = true)
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


}
