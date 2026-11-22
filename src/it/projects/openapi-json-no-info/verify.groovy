/*
 * Copyright MojoHaus and Contributors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import groovy.xml.XmlSlurper
import groovy.json.JsonSlurper

// without developers, licenses, organization or url the optional info sections are left out
File json = new File( basedir, 'target/site/servicedoc/OpenApi.json' )
assert json.exists()
def api = new JsonSlurper().parse( json, 'UTF-8' )

assert 'OpenAPI "JSON" Demo' == api.info.title
assert '1.0-SNAPSHOT' == api.info.version
assert 'This project declares no developers, licenses, organization or url.' == api.info.description
assert ['title', 'version', 'description'] == api.info.keySet() as List
assert 'my.service.com:8080/services/rest' == api.servers[0].url
assert ['/demo/v1/error', '/demo/v1/longs', '/demo/v1/string', '/demo/v1/string/{id}'] == api.paths.keySet() as List
assert ['void', 'DemoTo'] == api.components.schemas.keySet() as List

String yaml = new File( basedir, 'target/site/servicedoc/OpenApi.yaml' ).getText( 'UTF-8' ).replace( '\r\n', '\n' )
assert yaml.startsWith( 'openapi: 3.0.0\ninfo:\n  title: OpenAPI "JSON" Demo\n  version: 1.0-SNAPSHOT\n  description: This project' )
assert !yaml.contains( 'contact:' )
assert !yaml.contains( 'license:' )

for (name in ['OpenApi.json', 'OpenApi.yaml']) {
    def text = new File(basedir, 'target/site/servicedoc/' + name).getText( 'UTF-8' ).replace( '\r\n', '\n' )
    assert !text.contains('$services') : "unrendered Velocity reference in ${name}"
}
