<?xml version="1.0"?>
<!-- $Id$ -->
<document>
  <properties>
    <title>ServiceDocGen Maven Plugin</title>
  </properties>

  <body>
    <section name="ServiceDocGen Maven Plugin">
      ServiceDocGen Maven Plugin automatically generates detailes documentation of your services from the source code.
      It offers the following features:
      <ul>
        <li>Scan the project source code for JAX-RS annotated services</li>
        <li>Analysis on byte-code using <a href="https://github.com/m-m-m/util/">mmm-util-core</a> for reliable results.</li>
        <li>Analysis on source-code using <a href="https://github.com/paul-hammant/qdox#qdox">qdox</a> for automatic documentation from JavaDoc.</li>
        <li>Generate static HTML documentation that looks nice like <a href="http://swagger.io/">swagger</a> documentation.</li>
        <li>Generation based on templates using <a href="https://velocity.apache.org/">velocity</a>. You can also customize or localize the templates.</li>
        <li>Clear separation of analysis that collects metadata in an intermediate format and the actual generation.</li>
      </ul>
      <subsection name="Goals">
      <ul>
        <li><a href="generate-mojo.html">servicedocgen:generate</a> generates the documentation.</li>
      </ul>
      </subsection>
      <subsection name="Usage">
        <p>General instructions on how to use the Flatten Maven Plugin can be found on the <a href="usage.html">usage page</a>.</p>
        
        <p>In case you still have questions regarding the plugin's usage, please feel
            free to contact the <a href="mail-lists.html">user mailing list</a>. The posts to the mailing list are archived and could
            already contain the answer to your question as part of an older thread. Hence, it is also worth browsing/searching
            the <a href="mail-lists.html">mail archive</a>.
        </p>

       <p>If you feel like the plugin is missing a feature or has a defect, you can fill a feature request or bug report in our
          <a href="issue-tracking.html">issue tracker</a>. When creating a new issue, please provide a comprehensive description of your
          concern. Especially for fixing bugs it is crucial that the developers can reproduce your problem. For this reason,
          entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated.
          Of course, patches are most welcome too. Contributors can check out the project from our
          <a href="source-repository.html">source repository</a> and will find supplementary information in the
          <a href="http://maven.apache.org/guides/development/guide-helping.html">guide to helping with Maven</a>. 
       </p>
      
      </subsection>
    </section>
  </body>
</document>
