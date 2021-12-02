package tech.jhipster.config.apidoc;

import static org.springdoc.core.Constants.DEFAULT_GROUP_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfigProperties.GroupConfig;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

/**
 * <p>JHipsterOpenApiEndpoint class.</p>
 */
@WebEndpoint(id = "jhiopenapigroups")
public class JHipsterOpenApiEndpoint {

    private final SpringDocConfigProperties springDocConfigProperties;
    private final String appName;

    /**
     * <p>Constructor for JHipsterOpenApiEndpoint.</p>
     *
     * @param springDocConfigProperties a {@link org.springdoc.core.SpringDocConfigProperties} object.
     */
    public JHipsterOpenApiEndpoint(SpringDocConfigProperties springDocConfigProperties, String appName) {
        this.springDocConfigProperties = springDocConfigProperties;
        this.appName = appName;
    }

    /**
     * GET /management/jhiopenapigroups
     * <p>
     * Give openApi displayed on OpenApi page
     *
     * @return a Map with a String defining a category of openApi as Key and
     * another Map containing openApi related to this category as Value
     */
    @ReadOperation
    public List<Map<String, String>> allOpenApi() {
        return springDocConfigProperties.getGroupConfigs().stream().map(this::createGroupMap).collect(Collectors.toList());
    }

    private Map<String, String> createGroupMap(GroupConfig group) {
        Map<String, String> map = new HashMap<String, String>();
        String groupName = group.getGroup();
        map.put("group", groupName);
        String description = this.appName + " (" + (groupName == DEFAULT_GROUP_NAME ? "default" : groupName) + ")";
        map.put("description", description);
        return map;
    }
}
