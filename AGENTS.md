# Agent guide for servicedocgen-maven-plugin

`org.codehaus.mojo:servicedocgen-maven-plugin` documents the
JAX-RS services of a project: it reads JavaDoc from source and types from compiled classes, then writes
an HTML page, an OpenAPI description and a Swagger UI page to `target/site/servicedoc`. It requires
Maven 3.9.0+ and Java 8+; Maven 4 is not supported.

## Build and test

- Run `mvn verify`. CI calls the shared verify workflow in `mojohaus/.github` at `@master` with `maven4-enabled: false`, so Maven 4 is excluded from the matrix deliberately.
- The POM compiles against the Maven 3.9 line (`mavenVersion`), sets `minimalMavenBuildVersion` to 3.9.0 and declares a literal `<prerequisites><maven>3.9.0</maven>`; keep the compile baseline and the prerequisite distinct.
- Unit tests use JUnit 5. The integration tests are the projects in `src/it/projects`; the `run-its` profile, active unless `-DskipTests=true`, runs them with `maven-invoker-plugin` (`clean package`) against a mock repository served by `mrm-maven-plugin` from `src/it/mrm`.

## Documentation updates

A change that users or contributors can notice ships with the documentation it affects, in the
same pull request. A refactor with no visible effect needs none.

- A new parameter or default: the Javadoc of `ServiceDocGenReport`, which becomes the goal page,
  and the configuration list in `src/site/markdown/usage.md`.
- New or changed usage: `src/site/markdown/usage.md` or the sample in
  `src/site/markdown/examples/example-simple.md`. Only the first plugin snippet in `usage.md`
  carries a version, written as `<version><!-- latest release --></version>`; the others leave it
  out, so no example goes stale. The sample output
  `src/site/resources/examples/Service-Documentation.html` shows a released version, not a
  snapshot.
- A page added, removed or renamed: the menu in `src/site/site.xml`. Never rename a published
  page; its URL is linked from elsewhere.
- The Maven or Java requirement, or how to build and release: `README.md`.
- A new convention, build gate or test fixture: this file.

Check the result with `mvn site` and open `target/site/index.html`.
