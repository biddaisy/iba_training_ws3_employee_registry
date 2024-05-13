/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

/**
 * Class represents an Employee domain object
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Document(collection = "employees")
public record Employee (

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
) {}
