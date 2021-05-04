
# net.cactusthorn.config

The Java library with the goal of minimizing the code required to handle application configuration.

[![build](https://github.com/Gmugra/net.cactusthorn.config/actions/workflows/maven.yml/badge.svg)](https://github.com/Gmugra/net.cactusthorn.config/actions) [![Coverage Status](https://coveralls.io/repos/github/Gmugra/net.cactusthorn.config/badge.svg?branch=main)](https://coveralls.io/github/Gmugra/net.cactusthorn.config?branch=main) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Gmugra/net.cactusthorn.config.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Gmugra/net.cactusthorn.config/context:java) [![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/net.cactusthorn.config/core/0.10)](https://search.maven.org/search?q=g:net.cactusthorn.config) [![GitHub](https://img.shields.io/github/license/Gmugra/net.cactusthorn.config)](https://github.com/Gmugra/net.cactusthorn.config/blob/main/LICENSE) [![Build by Maven](http://maven.apache.org/images/logos/maven-feather.png)](http://maven.apache.org)

## Motivation

The inspiring idea for the project comes from [OWNER](https://github.com/lviggiano/owner). *OWNER* is a nice Java library for the same purpose, but it's future is "gray", because it not actually maintened anymore. So, this project is providing similar with *OWNER* API, but
1. Based not on Reflection, but on compile-time Code Generation (Java Annotation Processing)
1. Required at least Java 8, as result it support "more fresh" language features e.g. `java.util.Optional`
1. There is not goal, to provide *all* features of *OWNER*

### Basic usage

To access properties you need to define a convenient Java interface, e.g. :
```java
package my.superapp;

import static net.cactusthorn.config.core.Disable.Feature.*
import net.cactusthorn.config.core.*

import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Config
@Prefix("app")
interface MyConfig {

    @Default("unknown")
    String val();

    @Key("number")
    int intVal();

    @Disable(PREFIX)
    List<UUID> ids();

    @Split("[:;]")
    @Default("DAYS:HOURS")
    Optional<Set<TimeUnit>> units();
}
```
Based on this interface the annotations-processor will generate the implementation.
And all you need to do to get property values is to get an implementation using the `ConfigFactory`, e.g.:
```java
MyConfig myConfig =
    ConfigFactory.builder()
        .addSource("file:./myconfig.xml")
        .addSource("classpath:config/myconfig.properties", "system:properties")
        .build()
        .create(MyConfig.class);
```
e.g. "myconfig.properties":
```java
app.val=ABC
app.number=10
ids=f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454,123e4567-e89b-12d3-a456-556642440000
app.units=DAYS:HOURS;MICROSECONDS
```

### Maven/Gradle

Download: [Maven Central Repository](https://search.maven.org/search?q=g:net.cactusthorn.config):

In order to use the library in a project, it's need to add the dependency to the pom.xml:
```xml
<dependency>
    <groupId>net.cactusthorn.config</groupId>
    <artifactId>core</artifactId>
    <version>0.10</version>
</dependency>
```
It's also need to include the compiler used to convert annotated "source"-interfaces into the code:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
         <annotationProcessorPaths>
              <path>
                  <groupId>net.cactusthorn.config</groupId>
                  <artifactId>compiler</artifactId>
                  <version>0.10</version>
              </path>
         </annotationProcessorPaths>
    </configuration>
</plugin>

```
FYI: With this configuration, Maven will output the generated code into `target/generated-sources/annotations`.

Same with Gradle:
```
compile 'net.cactusthorn.config:core:0.10'
annotationProcessor 'net.cactusthorn.config:compiler:0.10'
```

### Annotations

1. `@Config`
   - `@Target(TYPE)`
   - The "source" interface must be annotated with this annotation.
1. `@Prefix`
   - `@Target(TYPE)`
   - Set global prefix for all property names
1. `@Key`
   - `@Target(METHOD)`
   - Set property name for the method. If this annotation is not present method-name will be used as property name
1. `@Default`
   - `@Target(METHOD)`
   - Set default value (if property will not found in sources, the default value will be used).
   - Can't be used for methods with `Optional` return type.
1. `@Disable`
   - `@Target(METHOD)`
   - Disable interface-level features for this method.
1. `@Split`
   - `@Target({TYPE,METHOD})`
   - Set splitter regular expression for splitting value for collections.
   - If this annotation is not present, default "splitter" is comma : `,`
1. `@ConverterClass`
   - `@Target(METHOD)`
   - apply custom converter implementation

### Supported method return types
The return type of the interface methods must either:
1. Be a primitive type
1. Have a public constructor that accepts a single `String` argument
1. Have a public static method named `valueOf` or `fromString` that accepts a single `String` argument
   1. e.g. [Integer.valueOf](https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html#valueOf-java.lang.String-)
   1. e.g. [UUID.fromString](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#fromString-java.lang.String-)
   1. If both methods are present then `valueOf` used unless the type is an `enum` in which case `fromString` used.
1. Be `java.net.URL` or `java.net.URI` or `java.time.Instant`
1. Be `List<T>`, `Set<T>` or `SortedSet<T>`, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.
1. Be `Optional<T>`, where T satisfies 2, 3, 4 or 5 above

### Custom converters
If it's need to deal with class which is not supported "by default" (see *Supported method return types*), a custom converter can be implemented and used.
The `@ConverterClass` annotation allows to specify a customized conversion logic implementing the `Converter` interface:
```java
public class DurationConverter implements Converter<Duration> {
    @Override public Duration convert(String value) {
        ...
    }
}
```
Usage:
```java
@Config public interface MyConfigWithConverter {
    @ConverterClass(DurationConverter.class) @Default("10 millisecond") Duration theDuration();

    @ConverterClass(DurationConverter.class) Optional<Duration> mayBeDuration();

    @ConverterClass(DurationConverter.class) Optional<List<Duration>> durations();
}
```
FYI: Custom converter implementation must be stateless and must have a default(no-argument) `public` constructor.

Converter implementations shipped with the library:
1. `DurationConverter` from [OWNER](http://owner.aeonbits.org/docs/type-conversion/)

### Interfaces inheritance
Interfaces inheritance is supported.
e.g.
```java
interface MyRoot {
    @Key(rootVal) String value();
}
```
```java
@Config
interface MyConfig extends MyRoot {
    int intValue();
}
```
- There is no limit to the number and "depth" of super-interfaces.
- Interface level annotations (e.g. `@Prefix`) on super-interfaces will be ignored.

### The `ConfigFactory`
The `ConfigFactory` is thread-safe, but not stateless. It stores loaded properties in the internal cache (see *Caching*).
Therefore, it certainly makes sense to create and use one single instance of `ConfigFactory` for the whole application.

### Direct access to properties
It's possible to get loaded propeties without define config-interface.
```java
ConfigHolder holder =
    ConfigFactory.builder()
        .setLoadStrategy(LoadStrategy.FIRST)
        .addSource("file:./myconfig.properties")
        .addSource("classpath:config/myconfig.properties", "system:properties")
        .build()
        .configHolder();

Integer intValue = holder.getInt("myIntKey");
char ch = holder.getChar("characterKey");
Optional<List<UUID>> list = holder.getOptionalList(UUID::fromString, "listKey", ",");
```

### Property not found : `@Default` or `Optional`
There are three ways for dealing with properties that are not found in sources:
1. If method return type is not `Optional` and the method do not annotated with `@Default`, the `ConfigFactory.create` method will throw runtime exception "property ... not found"
1. If method return type is `Optional` ->  method will return `Optional.empty()`
1. If method return type is not `Optional`, but the method do annotated with `@Default` -> method will return converted to return type deafult value.
FYI: The `@Default` annotation can't be used with a method that returns `Optional`.

### Standard loaders
1. System properties: `system:properties`
1. Environment variables: `system:env`
1. properties file from class-path : classpath:*relative-path-to-name*.properties[#charset]
   - Default charset (if URI fragment not present) is **UTF-8**
   - e.g. `classpath:config/my.properties#ISO-5589-1`
1. properties file from any URI convertable to URL: *whatever-what-supported*.properties[#charset]
   - Default charset (if URI fragment not present) is **UTF-8**
   - e.g. the file from the working directory: `file:./my.properties`
   - e.g. Windows file: `file:///C:/my.properties`
   - e.g. web: `https://raw.githubusercontent.com/Gmugra/net.cactusthorn.config/main/core/src/test/resources/test.properties`
   - e.g. jar in file-system: `jar:file:path/to/some.jar!/path/to/your.properties`
1. XML file from class-path : classpath:*relative-path-to-name*.xml[#charset]
   - XML format: [properties.dtd](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) or [OWNER](http://owner.aeonbits.org/docs/xml-support/)
   - Default charset (if URI fragment not present) is **UTF-8**
   - e.g. `classpath:config/my.xml#ISO-5589-1`
1. XML file from any URI convertable to URL: *whatever-what-supported*.xml[#charset]
   - XML format: [properties.dtd](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) or [OWNER](http://owner.aeonbits.org/docs/xml-support/)
   - Default charset (if URI fragment not present) is **UTF-8**
   - e.g. `file:./my.xml`

### Custom loaders
It's possible to implement custom loaders using `Loader` interface.
This makes it possible to load properties from specific sources (e.g. Database, ZooKeeper and so on) or to support alternative configuration file formats (e.g. JSON).
e.g.
```java
public final class SinglePropertyLoader implements Loader {
    @Override public boolean accept(URI uri) {
        return uri.toString().equals("single:property");
    }

    @Override public Map<String, String> load(URI uri, ClassLoader classLoader) {
        Map<String, String> result = new HashMap<>();
        result.put("key", "value");
        return result;
    }
}
```
```java
ConfigFactory factory =
    ConfigFactory.builder()
    .addLoader(new SinglePropertyLoader())
    .addSource("single:property")
    .build();
```
FYI:
   - Custom loaders always have the highest priority: last added -> first used.
   - Custom loader implementation must be stateless and must have a default(no-argument) `public` constructor.

### System properties and/or environment variable in sources URIs

Syntax: {*name*}

e.g.
- `file:/{config-path}/my.properties`
- `classpath:{config-path}/my.properties#{charset}`

FYI: If a system property or environment variable does not exist, an *empty string* will be used as the value.

Special use-case *user home directory*: The URIs with `file:~/` (e.g. `file:~/my.xml` or `jar:file:~/some.jar!/your.properties`) always correctly resolved to user home directory independent from OS.
- e.g. in Windows, URI `file:~/my.xml` will be replaced to `file:///C:/Users/UserName/my.xml`.

### Loading strategies
ConfigFactory saves the sequence in which the sources URIs were added.
```java
MyConfig myConfig =
    ConfigFactory.builder()
        .setLoadStrategy(LoadStrategy.FIRST)
        .addSource("file:/myconfig.properties", "classpath:config/myconfig.properties")
        .build()
        .create(MyConfig.class);
```
Loading strategies:
- **FIRST** - only the first (in the sequence of adding) existing and not empty source will be used.
- **MERGE** - merging all properties from first added to last added.
- **FIRST_KEYCASEINSENSITIVE** - same with **FIRST**, but property keys are case insensitive
- **MERGE_KEYCASEINSENSITIVE** - same with **MERGE**, but property keys are case insensitive
- Default strategy is **MERGE**

Manually added properties (which added using `ConfigFactory.Builder.setSource(Map<String, String> properties)` method) are highest priority always.
So, loaded by URIs properties merged with manually added properties, independent of loading strategy.

### java.io.Serializable
"config"-interface can extends (directly or over super-interface) `java.io.Serializable`.
In this case generated class will also get `private static final long serialVersionUID` attribute.
```java
@Config
public interface MyConfig extends java.io.Serializable {
    long serialVersionUID = 100L;

    String val();
}
```
The interface (as in the example before) can, optionally, contains `long serialVersionUID` constant.
If the constant is present, the value will be used for the `private static final long serialVersionUID` attribute in the generated class.
Otherwise generated class will be generated with `private static final long serialVersionUID = 0L`.

### Caching
By default, `ConfigFactory` caches loaded properties using source-URI (after resolving system properties and/or environment variable in it) as a cache key.
To not cache properties related to the URI(s), use the `addSourceNoCache` methods instead of `addSource`.

### Logging
The runtime part of the library is using [Java Logging API](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html).
That's because one of the requirements is that external libraries must not be used, and JUL is only option in this case.
However, JUL is rarely chosen for productive use, so, in the application which is using this library, it need to care about to redirect JUL calls to the logging API which is using in the application.

e.g., in case of [SLF4J](http://www.slf4j.org/), which is, looks like, the most popular at the moment, you need next dependency:
```xml
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>jul-to-slf4j</artifactId>
	<version>1.7.30</version>
</dependency>
```
and e.g. this code somewhere at start of the application:
```java
// java.util.logging -> SLF4j
org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger();
org.slf4j.bridge.SLF4JBridgeHandler.install();
java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINEST);
```

## FYI : Eclipse
It does not have annotation-processing enabled by default. To get it, you must install *m2e-apt* from the eclipse marketplace: https://immutables.github.io/apt.html

## LICENSE
net.cactusthorn.config is released under the BSD 3-Clause license. See LICENSE file included for the details.

