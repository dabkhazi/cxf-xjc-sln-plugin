# SingularListNamePlugin

## Описание

`SingularListNamePlugin` — это расширение для `xjc`, устраняющее автоматическое преобразование имён полей коллекций в множественное число, которое происходит при использовании `xjc:simple` биндинга.

## Проблема

При генерации классов JAXB с включённым `xjc:simple` биндингом поля коллекций получают множественные имена:

```java
@XmlElement(name = "point", required = true)
protected List<Point> points; // "points" вместо "point"
```

Также автоматически создается методы `getPoints()`, что может быть неудобно для работы с доменной моделью.

## Решение

Этот плагин:

- Переименовывает поля `List<T>` в **единственное число** на основе значения из `@XmlElement(name = "...")`
- Обновляет `@XmlType(propOrder = {...})`, чтобы сохранить порядок с учётом новых имён
- Переименовывает геттеры `getFoos()` → `getFoo()`

## Использование

Добавь плагин в `xjc`-генерацию, например:

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

## Как собрать

```bash
mvn clean install
```

JAR-файл плагина будет доступен в `sln/target/`.

## Пример результата

```java
@XmlElement(name = "point", required = true)
protected List<Point> point;

public List<Point> getPoint() {
    return point;
}
```
