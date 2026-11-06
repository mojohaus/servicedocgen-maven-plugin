title: ServiceDocGen Maven Plugin

# ServiceDocGen Maven Plugin

ServiceDocGen Maven Plugin automatically generates detailes documentation of your services from the source code.
It offers the following features:

- Scan the project source code for JAX-RS annotated services
- Analysis on byte-code using [mmm-util-core](https://github.com/m-m-m/util/) for reliable results.
- Analysis on source-code using [qdox](https://github.com/paul-hammant/qdox#qdox) for automatic documentation from JavaDoc.
- Generate static HTML documentation that looks nice like [swagger](http://swagger.io/) documentation.
- Generation based on templates using [velocity](https://velocity.apache.org/). You can also customize or localize the templates.
- Clear separation of analysis that collects metadata in an intermediate format and the actual generation.

## Goals

- [servicedocgen:generate](generate-mojo.html) generates the documentation.

## Usage

General instructions on how to use the Flatten Maven Plugin can be found on the [usage page](usage.html).

In case you still have questions regarding the plugin's usage, please feel
free to contact the [user mailing list](mail-lists.html). The posts to the mailing list are archived and could
already contain the answer to your question as part of an older thread. Hence, it is also worth browsing/searching
the [mail archive](mail-lists.html).

If you feel like the plugin is missing a feature or has a defect, you can fill a feature request or bug report in our
[issue tracker](issue-tracking.html). When creating a new issue, please provide a comprehensive description of your
concern. Especially for fixing bugs it is crucial that the developers can reproduce your problem. For this reason,
entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated.
Of course, patches are most welcome too. Contributors can check out the project from our
[source repository](source-repository.html) and will find supplementary information in the
[guide to helping with Maven](http://maven.apache.org/guides/development/guide-helping.html).
