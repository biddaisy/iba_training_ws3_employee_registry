/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.common.service;

import eu.ibagroup.common.mongo.SessionRepository;
import eu.ibagroup.common.mongo.collection.Session;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * A Service to work with Session objects
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public boolean isChatNew(Long chatId) {
        return sessionRepository.findOneByChatId(chatId) == null;
    }

    public void initChat(Long chatId) {
        sessionRepository.save(new Session(chatId));
    }

    public void persist(Session session) {
        sessionRepository.save(session);
    }

    public long count() {
        return sessionRepository.count();
    }

    public boolean isUserVerified(Long chatId) {
        val session = sessionRepository.findOneByChatId(chatId);
        return session != null && session.isContactVerified();
    }

    public Session getSession(Long chatId) {
        return sessionRepository.findOneByChatId(chatId);
    }

    public void deleteSession(Long chatId) {
        sessionRepository.deleteById(chatId);
    }

    public boolean initIfNewChat(Long chatId) {
        // check if conversation has started, if not -- start conversation
        if (isChatNew(chatId)) {
            initChat(chatId);
            return true;
        }
        return false;
    }

}
