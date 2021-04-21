package net.cactusthorn.config.compiler;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.cactusthorn.config.compiler.methodvalidator.MethodInfo;
import net.cactusthorn.config.compiler.methodvalidator.MethodInfo.StringMethod;
import net.cactusthorn.config.core.ConfigBuilder;

public final class ConfigBuilderGenerator extends Generator {

    private final ClassName configClass;

    ConfigBuilderGenerator(TypeElement interfaceElement, List<MethodInfo> methodsInfo) {
        super(interfaceElement, methodsInfo, ConfigBuilder.BUILDER_CLASSNAME_PREFIX);
        configClass = ClassName.get(packageName(), ConfigGenerator.CLASSNAME_PREFIX + interfaceName().simpleName());
    }

    private static final ClassName CONFIG_BUILDER = ClassName.get(ConfigBuilder.class);

    @Override JavaFile generate() {
        TypeName superClass = ParameterizedTypeName.get(CONFIG_BUILDER, configClass);
        TypeSpec.Builder classBuilder = classBuilder().superclass(superClass);
        addEnum(classBuilder);
        addKey(classBuilder);
        addDefault(classBuilder);
        addConstructor(classBuilder);
        addBuild(classBuilder);

        return JavaFile.builder(packageName(), classBuilder.build()).build();
    }

    private static final String METHOD_ENUM_NAME = "Method";

    private void addEnum(TypeSpec.Builder classBuilder) {
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(METHOD_ENUM_NAME).addModifiers(Modifier.PRIVATE);
        methodsInfo().forEach(mi -> enumBuilder.addEnumConstant(mi.name()));
        classBuilder.addType(enumBuilder.build());
    }

    private static final ClassName MAP = ClassName.get(Map.class);
    private static final ClassName STRING = ClassName.get(String.class);
    private static final TypeName MAP_STRING = ParameterizedTypeName.get(MAP, STRING, STRING);

    private static final String KEYS_MAP_NAME = "KEYS";

    private void addKey(TypeSpec.Builder classBuilder) {
        ClassName methodsEnumName = ClassName.get(packageName(), className() + '.' + METHOD_ENUM_NAME);
        TypeName mapTypeName = ParameterizedTypeName.get(MAP, methodsEnumName, STRING);

        FieldSpec fieldSpec = FieldSpec.builder(mapTypeName, KEYS_MAP_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build();

        CodeBlock.Builder blockBuilder = CodeBlock.builder().addStatement("$L = new $T<>($L.class)", KEYS_MAP_NAME, EnumMap.class,
                METHOD_ENUM_NAME);
        methodsInfo().forEach(mi -> blockBuilder.addStatement("$L.put($L.$L, $S)", KEYS_MAP_NAME, METHOD_ENUM_NAME, mi.name(), mi.key()));

        classBuilder.addField(fieldSpec);
        classBuilder.addStaticBlock(blockBuilder.build());
    }

    private static final String DEFAULTS_MAP_NAME = "DEFAULTS";

    private void addDefault(TypeSpec.Builder classBuilder) {
        ClassName methodsEnumName = ClassName.get(packageName(), className() + '.' + METHOD_ENUM_NAME);
        TypeName mapTypeName = ParameterizedTypeName.get(MAP, methodsEnumName, STRING);

        FieldSpec fieldSpec = FieldSpec.builder(mapTypeName, DEFAULTS_MAP_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build();

        CodeBlock.Builder blockBuilder = CodeBlock.builder().addStatement("$L = new $T<>($L.class)", DEFAULTS_MAP_NAME, EnumMap.class,
                METHOD_ENUM_NAME);
        methodsInfo().forEach(mi -> mi.defaultValue()
                .ifPresent(d -> blockBuilder.addStatement("$L.put($L.$L, $S)", DEFAULTS_MAP_NAME, METHOD_ENUM_NAME, mi.name(), d)));

        classBuilder.addField(fieldSpec);
        classBuilder.addStaticBlock(blockBuilder.build());
    }

    private static final String PROPERTIES = "properties";

    private void addConstructor(TypeSpec.Builder classBuilder) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(MAP_STRING, PROPERTIES, Modifier.FINAL).addStatement("super($L)", PROPERTIES);

        classBuilder.addMethod(constructorBuilder.build());
    }

    private void addBuild(TypeSpec.Builder classBuilder) {
        MethodSpec.Builder buildBuilder = MethodSpec.methodBuilder("build").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
                .returns(configClass);

        methodsInfo().forEach(mi -> buildBuilder.addStatement("$T $L = $L", mi.returnTypeName(), mi.name(), convert(mi)));

        String parameters = methodsInfo().stream().map(mi -> mi.name()).collect(Collectors.joining(", "));

        classBuilder.addMethod(buildBuilder.addStatement("return new $T($L)", configClass, parameters).build());
    }

    private CodeBlock convert(MethodInfo mi) {
        CodeBlock.Builder builder = findGetMethod(mi).add("(");
        CodeBlock defaultValue = defaultValue(mi);
        if (mi.returnStringMethod().isPresent()) {
            MethodInfo.StringMethodInfo smi = mi.returnStringMethod().get();
            StringMethod sm = smi.stringMethod();
            if (sm == StringMethod.STRING) {
                builder.add("s -> s, ");
            } else if (sm == StringMethod.CONSTRUCTOR) {
                builder.add("s -> new $T(s), ", mi.returnTypeName());
            } else {
                builder.add("$T::$L, ", smi.methodType(), sm.methodName().get());
            }
            return builder.add(key(mi)).add(split(mi)).add(defaultValue).add(")").build();
        }
        return builder.add("$T::valueOf, ", mi.returnTypeName().box()).add(key(mi)).add(defaultValue).add(")").build();
    }

    protected CodeBlock.Builder findGetMethod(MethodInfo mi) {
        if (mi.returnOptional()) {
            return CodeBlock.builder().add(mi.returnInterface().map(t -> {
                if (t == List.class) {
                    return "getOptionalList";
                }
                if (t == Set.class) {
                    return "getOptionalSet";
                }
                return "getOptionalSortedSet";
            }).orElse("getOptional"));
        }
        return CodeBlock.builder().add(mi.returnInterface().map(t -> {
            if (t == List.class) {
                return mi.defaultValue().map(d -> "getListDefault").orElse("getList");
            }
            if (t == Set.class) {
                return mi.defaultValue().map(d -> "getSetDefault").orElse("getSet");
            }
            return mi.defaultValue().map(d -> "getSortedSetDefault").orElse("getSortedSet");
        }).orElse(mi.defaultValue().map(d -> "getDefault").orElse("get")));
    }

    private CodeBlock key(MethodInfo mi) {
        return CodeBlock.of("$L.get($L.$L)", KEYS_MAP_NAME, METHOD_ENUM_NAME, mi.name());
    }

    private CodeBlock split(MethodInfo mi) {
        return mi.returnInterface().map(i -> CodeBlock.of(", $S", mi.split())).orElse(CodeBlock.of(""));
    }

    private CodeBlock defaultValue(MethodInfo mi) {
        return mi.defaultValue().map(s -> CodeBlock.of(", $L.get($L.$L)", DEFAULTS_MAP_NAME, METHOD_ENUM_NAME, mi.name()))
                .orElse(CodeBlock.of(""));
    }
}