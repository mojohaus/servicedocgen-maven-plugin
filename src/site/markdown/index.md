title: ServiceDocGen Maven Plugin

# ServiceDocGen Maven Plugin

ServiceDocGen Maven Plugin automatically generates detailed documentation of your services from the source code.
It offers the following features:

- Scan the project source code for JAX-RS annotated services
- Analysis on byte-code using [mmm-util-core](https://github.com/m-m-m/util/) for reliable results.
- Analysis on source-code using [qdox](https://github.com/paul-hammant/qdox#qdox) for automatic documentation from JavaDoc.
- Generate static HTML documentation that looks nice like [swagger](https://swagger.io/) documentation.
- Generation based on templates using [velocity](https://velocity.apache.org/). You can also customize or localize the templates.
- Generate an OpenAPI description (`OpenApi.yaml`) and a Swagger UI page next to the HTML documentation.
- Clear separation of analysis that collects metadata in an intermediate format and the actual generation.

## Requirements

The plugin requires Maven 3.9.0 or later and Java 8 or later. Maven 4 is not supported. To run it as a report during
`mvn site`, use maven-site-plugin 3.20.0 or later.

## Goals

- [servicedocgen:generate](generate-mojo.html) generates the documentation into `target/site/servicedoc`.

## Usage

General instructions on how to use the ServiceDocGen Maven Plugin can be found on the [usage page](usage.html).

In case you still have questions regarding the plugin's usage, please feel
free to contact the [user mailing list](mailing-lists.html). The posts to the mailing list are archived and could
already contain the answer to your question as part of an older thread. Hence, it is also worth browsing/searching
the [mail archive](mailing-lists.html).

If you feel like the plugin is missing a feature or has a defect, you can fill a feature request or bug report in our
[issue tracker](issue-management.html). When creating a new issue, please provide a comprehensive description of your
concern. Especially for fixing bugs it is crucial that the developers can reproduce your problem. For this reason,
entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated.
Of course, patches are most welcome too. Contributors can check out the project from our
[source repository](scm.html) and will find supplementary information in the
[guide to helping with Maven](https://maven.apache.org/guides/development/guide-helping.html).
