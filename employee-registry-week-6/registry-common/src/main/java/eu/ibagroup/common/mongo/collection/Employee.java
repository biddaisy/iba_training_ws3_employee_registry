/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class represents an Employee domain object
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "employees")
public class Employee {

    @Id
    private String _id;

    @NonNull
    private String firstName;

    private String middleName;

    @NonNull
    private String lastName;

    @NonNull
    private String firstNameEn;

    @NonNull
    private String lastNameEn;

    private String position;

    private String companyBranch;

    private String email;

    private String photo;

    @JsonIgnore
    private String phoneHash;
}
