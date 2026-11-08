# MojoHaus ServiceDocGen Maven Plugin

This is the [servicedocgen-maven-plugin](https://www.mojohaus.org/servicedocgen-maven-plugin/).

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/mojohaus/servicedocgen-maven-plugin.svg?label=License)](https://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/servicedocgen-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.mojo/servicedocgen-maven-plugin)
[![GitHub CI](https://github.com/mojohaus/servicedocgen-maven-plugin/actions/workflows/maven.yml/badge.svg)](https://github.com/mojohaus/servicedocgen-maven-plugin/actions/workflows/maven.yml)

The plugin documents the JAX-RS services of a project. It reads the JavaDoc from the source code and the types from
the compiled classes, and writes an HTML page, an OpenAPI description and a Swagger UI page to
`target/site/servicedoc`.

It needs Maven 3.9.0 or later and Java 8 or later. Maven 4 is not supported yet.

## Usage

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>servicedocgen-maven-plugin</artifactId>
      <version><!-- latest version from Maven Central --></version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

The `generate` goal runs in the `prepare-package` phase. To run it on its own:

```
mvn compile servicedocgen:generate
```

The plugin also works as a report: declared under `<reporting>`, it writes the same files during `mvn site`. This needs
maven-site-plugin 3.20.0 or later.

See the [usage page](https://www.mojohaus.org/servicedocgen-maven-plugin/usage.html) for the configuration, the
[goal reference](https://www.mojohaus.org/servicedocgen-maven-plugin/generate-mojo.html) for every parameter, and the
[FAQ](https://www.mojohaus.org/servicedocgen-maven-plugin/faq.html).

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```

