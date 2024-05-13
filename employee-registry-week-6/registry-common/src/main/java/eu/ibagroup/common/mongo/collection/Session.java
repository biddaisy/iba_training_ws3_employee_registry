/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

/**
 * Class represents a Session domain object.
 * It encapsulates user session with TG bot.
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "sessions")
public class Session {

    @Id
    @NonNull
    private Long chatId;

    @Field(targetType = FieldType.DATE_TIME)
    private LocalDateTime verifiedDate;

    @Field(targetType = FieldType.BOOLEAN)
    private boolean isContactVerified;

    @Field(targetType = FieldType.STRING)
    private String phoneHash;

}
