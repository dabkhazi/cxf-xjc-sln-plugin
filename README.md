# SingularListNamePlugin

## Description

`SingularListNamePlugin` is an `xjc` extension that prevents collection field names from being automatically pluralized when the `xjc:simple` binding is used.

## Problem

When JAXB classes are generated with the `xjc:simple` binding enabled, collection fields receive plural names:

```java
@XmlElement(name = "point", required = true)
protected List<Point> points; // "points" instead of "point"
```

The generated accessor is also named `getPoints()`, which can be inconvenient when working with a domain model.

## Solution

This plugin:

- Renames `List<T>` fields to the **singular form** based on the value of `@XmlElement(name = "...")`
- Updates `@XmlType(propOrder = {...})` to preserve the order after field names are changed
- Renames getters from `getFoos()` to `getFoo()`

## Usage

Add the plugin to `xjc` generation, for example:

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-xjc-plugin</artifactId>
    <version>4.1.0</version>
    <executions>
        <execution>
         <id>generate-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>xsdtojava</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <xsdOptions>
            <xsdOption>
                <extension>true</extension>
                <xsd>${basedir}/src/test/resources/schemas/configuration/foo.xsd</xsd>
                <bindingFile>${basedir}/src/test/resources/schemas/configuration/foo.xjb</bindingFile>
                <extensionArgs>
                    <arg>-Xsingular-names</arg>
                </extensionArgs>
            </xsdOption>
        </xsdOptions>
        <extensions>
            <extension>io.github.dabkhazi:cxf-xjc-singular-names:1.0.0</extension>
        </extensions>
    </configuration>
</plugin>
```

## Build

```bash
mvn clean install
```

The plugin JAR file will be available in `sln/target/`.

## Example Output

```java
@XmlElement(name = "point", required = true)
protected List<Point> point;

public List<Point> getPoint() {
    return point;
}
```
