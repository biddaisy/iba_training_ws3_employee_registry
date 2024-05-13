/*
 * IBA Java Workshop v3.0
 */
package eu.ibagroup.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller returns application version, can be used as liveness probe
 *
 *  @author Mikalai Zaikin (mzaikin@ibagroup.eu)
 *  @since 4Q2023
 */
@RestController
public class VersionController {

    @Value("${git.tags}")
    private String tag;

    @Value("${git.commit.id}")
    private String commitId;

    @RequestMapping("/version")
    public Map<String, String> getCommitId() {
        Map<String, String> result = new HashMap<>();
        result.put("Version", tag);
        result.put("Commit", commitId);
        return result;
    }

}
