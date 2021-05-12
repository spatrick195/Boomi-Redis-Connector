package nz.co.fmg.redis;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseBrowser;
import nz.co.fmg.redis.Utils.Constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RedisBrowser extends BaseBrowser {
    List<String> objectTypes = Arrays.asList(
            "GET",
            "CREATE",
            "DELETE"
    );

    protected RedisBrowser(RedisConnection connection) {
        super(connection);
    }

    @Override
    public ObjectTypes getObjectTypes() {
        try {
            ObjectTypes objectTypes = new ObjectTypes();
            for (String stringType : this.objectTypes) {
                ObjectType type = new ObjectType();
                type.setId(stringType);
                objectTypes.getTypes().add(type);
            }
            return objectTypes;
        } catch (Exception e) {
            throw new ConnectorException(Constants.HTTP_500, e.getMessage(), e.getCause());
        }
    }

    @Override
    public ObjectDefinitions getObjectDefinitions(String objectTypeId, Collection<ObjectDefinitionRole> roles) {
        try {
            ObjectDefinition definition = new ObjectDefinition();
            ObjectDefinitions definitions = new ObjectDefinitions();
            definition.setElementName("");
            definition.setInputType(ContentType.NONE);
            definition.setOutputType(ContentType.NONE);
            definitions.getDefinitions().add(definition);
            return definitions;
        } catch (Exception e) {
            throw new ConnectorException(Constants.HTTP_500, e.getMessage(), e.getCause());
        }
    }
}
