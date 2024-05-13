/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.mongo;

import eu.ibagroup.common.mongo.collection.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository interface, provides basic finder for Sessions
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
public interface SessionRepository extends MongoRepository<Session, Long> {
    Session findOneByChatId(Long chatId);
}
