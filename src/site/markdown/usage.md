title: Usage

# Usage

Simply add the plugin to the build section of your POM for the module containing services as following. The plugin
requires Maven 3.9.0 or later and Java 8 or later.

```xml
<build>
  <plugins>
    ...
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>servicedocgen-maven-plugin</artifactId>
      <version><!-- latest release --></version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <descriptor>
          <info>
            <title>Service Documentation</title>
            <description>This is ...</description>
          </info>
          <host>my.service.com</host>
          <port>8443</port>
          <basePath>myapp/services/rest</basePath>
          <schemes>
            <scheme>https</scheme>
          </schemes>
          <javadocs>
            <javadoc>
              <url>https://my.service.com/apidocs</url>
            </javadoc>
          </javadocs>
        </descriptor>
      </configuration>
    </plugin>
  </plugins>
</build>
```

## Result

The `generate` goal runs in the `prepare-package` phase. After you called `mvn package` you will find the generated
documentation in `target/site/servicedoc/`:

- `index.html` is the documentation of the services.
- `OpenApi.yaml` is the OpenAPI description.
- `SwaggerUI.html` shows the OpenAPI description with Swagger UI.

To run the goal on its own, use `mvn compile servicedocgen:generate`.

## Configuration

- `classnameRegex` (default `.*Service.*`) selects the classes by their simple name; a class also needs a `@Path`
  annotation to be documented. `serviceClassName` documents the one class with that fully qualified name instead.
- `reportFolder` (default `servicedoc`) is the folder below `target/site` that receives the files.
- `introspectFields` set to `true` introspects the fields of Java beans instead of their getters.
- `descriptor` holds what the source code cannot tell: the title and description in `info`, `host`, `port`,
  `basePath`, `schemes`, `javadocs` to link types to Javadoc, and `errors` to document the exceptions that services
  throw. The [simple project](./examples/example-simple.html) shows them.
- `templates` replaces the default list of templates, one `template` with a `templateName` and an optional
  `outputName` for each file to generate, and `templatePath` is the class path location they are read from. Use them
  to customize or localize the output.

## Report

The plugin implements a Maven report. Declared under `<reporting>`, it writes the same files during `mvn site`, which
needs maven-site-plugin 3.20.0 or later.
