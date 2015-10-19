# MojoHaus ServicedocGen Maven Plugin

This is the [servicedocgen-maven-plugin](http://www.mojohaus.org/servicedocgen-maven-plugin/).
 
[![Build Status](https://travis-ci.org/mojohaus/servicedocgen-maven-plugin.svg?branch=master)](https://travis-ci.org/mojohaus/servicedocgen-maven-plugin)

## Quickstart
This plugin automatically generates a nice documentation for your REST-Services from JAX-RS annotated classes or interfaces.
```
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>servicedocgen-maven-plugin</artifactId>
        <!--<version>INSERT LATEST VERSION HERE</version>-->
        <executions>
          <execution>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <!-- See usage on maven site from link above for details -->
        </configuration>
      </plugin>
    </plugins>
  </build>
```

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```
