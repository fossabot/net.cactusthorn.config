package net.cactusthorn.config.compiler.methodvalidator;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import net.cactusthorn.config.compiler.Annotations;
import net.cactusthorn.config.compiler.InterfaceInfo;
import net.cactusthorn.config.core.Disable;

public final class MethodInfo {

    public enum StringMethod {
        STRING(Optional.empty()), VALUEOF(Optional.of("valueOf")), FROMSTRING(Optional.of("fromString")), CONSTRUCTOR(Optional.empty());

        public static final Set<String> METHODS = Stream.of(values()).map(sm -> sm.methodName()).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toSet());

        private final Optional<String> methodName;

        StringMethod(Optional<String> methodName) {
            this.methodName = methodName;
        }

        public Optional<String> methodName() {
            return methodName;
        }

        public static StringMethod fromMethodName(String name) {
            // @formatter:off
            return Stream.of(values())
                .filter(sm -> sm.methodName().filter(name::equals).isPresent())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException());
            // @formatter:on
        }
    }

    public static final class StringMethodInfo {
        private final StringMethod stringMethod;
        private final TypeName methodType;

        private StringMethodInfo(StringMethod stringMethod, TypeMirror stringMethodTM) {
            this.stringMethod = stringMethod;
            this.methodType = TypeName.get(stringMethodTM);
        }

        public StringMethod stringMethod() {
            return stringMethod;
        }

        public TypeName methodType() {
            return methodType;
        }
    }

    private final Annotations annotations;
    private final TypeName returnTypeName;
    private final String name;
    private final Set<Disable.Feature> disabledFeatures;
    private final Optional<String> defaultValue;

    private String key;
    private String split;

    private Optional<StringMethodInfo> returnStringMethod = Optional.empty();
    private Optional<Type> returnInterface = Optional.empty();
    private boolean returnOptional = false;

    MethodInfo(ExecutableElement methodElement) {
        annotations = new Annotations(methodElement);
        returnTypeName = ClassName.get(methodElement.getReturnType());
        name = methodElement.getSimpleName().toString();
        key = annotations.key().orElse(name);
        disabledFeatures = annotations.disable();
        defaultValue = annotations.defaultValue();
    }

    MethodInfo withStringMethod(StringMethod stringMethod, TypeMirror stringMethodTM) {
        returnStringMethod = Optional.of(new StringMethodInfo(stringMethod, stringMethodTM));
        return this;
    }

    MethodInfo withInterface(Type interfaceType) {
        returnInterface = Optional.of(interfaceType);
        return this;
    }

    MethodInfo withOptional() {
        returnOptional = true;
        return this;
    }

    public MethodInfo withInterfaceInfo(InterfaceInfo interfaceInfo) {
        if (!disabledFeatures.contains(Disable.Feature.PREFIX)) {
            key = interfaceInfo.prefix() + key;
        }
        split = annotations.split().orElse(interfaceInfo.split());
        return this;
    }

    public String key() {
        return key;
    }

    public String name() {
        return name;
    }

    public TypeName returnTypeName() {
        return returnTypeName;
    }

    public Optional<Type> returnInterface() {
        return returnInterface;
    }

    public boolean returnOptional() {
        return returnOptional;
    }

    public Optional<StringMethodInfo> returnStringMethod() {
        return returnStringMethod;
    }

    public String split() {
        return split;
    }

    public Optional<String> defaultValue() {
        return defaultValue;
    }
}