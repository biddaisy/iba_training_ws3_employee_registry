package eu.ibagroup.web.git;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitService {
    public static final String GIT_TAGS_KEY = "GIT_TAGS";
    public static final String GIT_COMMIT_ID_ABBREV_KEY = "GIT_COMMIT_ID_ABBREV";

    @Value("${git.tags}")
    private final String gitTags;
    @Value("${git.commit.id.abbrev}")
    private final String gitCommitIdAbbrev;

    public Map<String, String> getVersion() {
        return Map.of(GIT_TAGS_KEY, gitTags, GIT_COMMIT_ID_ABBREV_KEY, gitCommitIdAbbrev);
    }
}
