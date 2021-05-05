package net.cactusthorn.config.compiler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public interface GeneratorPart {

    ClassName MAP = ClassName.get(Map.class);
    ClassName CONCURRENTHASHMAP = ClassName.get(ConcurrentHashMap.class);

    ClassName OBJECT = ClassName.get(Object.class);
    ClassName STRING = ClassName.get(String.class);

    TypeName MAP_STRING_OBJECT = ParameterizedTypeName.get(MAP, STRING, OBJECT);
    TypeName MAP_STRING_STRING = ParameterizedTypeName.get(MAP, STRING, STRING);
    TypeName CONCURRENTHASHMAP_STRING_OBJECT = ParameterizedTypeName.get(CONCURRENTHASHMAP, STRING, OBJECT);

    String VALUES_ATTR = "VALUES";
    String DEFAULTS_ATTR = "DEFAULTS";

    void addPart(TypeSpec.Builder classBuilder, Generator generator);
}
