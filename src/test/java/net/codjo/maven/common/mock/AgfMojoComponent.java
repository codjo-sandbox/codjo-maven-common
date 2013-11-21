package net.codjo.maven.common.mock;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class AgfMojoComponent {
    private static final Map components = new HashMap();


    private AgfMojoComponent() {
    }


    public static void declareComponent(Object mock) {
        components.put(mock.getClass(), mock);
    }


    public static Object getComponent(Class mockClass) {
        return components.get(mockClass);
    }


    public static Map getComponents() {
        return components;
    }
}
