/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Class represents an Employee domain object
 *
 * @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 * @since 4Q2023
 */
@Document(collection = "employees")
public record Employee(

        @Id
        String _id,

        @NonNull
        String firstName,

        String middleName,

        @NonNull
        String lastName,

        @NonNull
        String firstNameEn,

        @NonNull
        String lastNameEn,

        String position,

        String companyBranch,

        String email,

        String photo,

        @JsonIgnore
        String phoneHash

) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(_id, employee._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
