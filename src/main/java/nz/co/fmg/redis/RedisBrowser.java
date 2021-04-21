package nz.co.fmg.redis;

import com.boomi.connector.api.*;
import com.boomi.connector.util.BaseBrowser;

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
            throw new ConnectorException(e);
        }
    }

    @Override
    public ObjectDefinitions getObjectDefinitions(String objectTypeId, Collection<ObjectDefinitionRole> roles) {
        try {
            ObjectDefinitions definitions = new ObjectDefinitions();
            ObjectDefinition definition = new ObjectDefinition();

            definition.setElementName("");
            definition.setInputType(ContentType.NONE);
            definition.setOutputType(ContentType.NONE);
            definitions.getDefinitions().add(definition);

            return definitions;
        } catch (Exception e) {
            throw new ConnectorException(e);
        }
    }
}
