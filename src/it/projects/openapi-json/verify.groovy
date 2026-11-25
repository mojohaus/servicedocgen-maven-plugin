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

// the JSON template must produce a document a JSON parser accepts, with and without contact and license
File json = new File(basedir, 'target/site/servicedoc/OpenApi.json')
assert json.exists()
def api = new JsonSlurper().parse(json, 'UTF-8')

assert '3.0.0' == api.openapi
assert 'OpenAPI "JSON" Demo' == api.info.title
assert '1.0-SNAPSHOT' == api.info.version
assert 'Contact and license come from the POM\'s developers and licenses.' == api.info.description
assert 'J\u00f6rg Hohwiller' == api.info.contact.name
assert 'https://github.com/hohwille' == api.info.contact.url
assert 'joerg.hohwiller@googlemail.com' == api.info.contact.email
assert 'Apache Software Licenese' == api.info.license.name
assert 'http://www.apache.org/licenses/LICENSE-2.0.txt' == api.info.license.url
assert 'my.service.com:8080/services/rest' == api.servers[0].url

assert ['/demo/v1/error', '/demo/v1/longs', '/demo/v1/string', '/demo/v1/string/{id}'] == api.paths.keySet() as List
assert ['delete', 'get'] == api.paths['/demo/v1/string/{id}'].keySet().sort()
assert 'get__string__id_' == api.paths['/demo/v1/string/{id}'].get.operationId
assert 'id' == api.paths['/demo/v1/string/{id}'].get.parameters[0].name
assert 'integer' == api.paths['/demo/v1/string/{id}'].get.parameters[0].schema.type
assert ['204', '500'] == api.paths['/demo/v1/error'].get.responses.keySet() as List
assert '#/components/schemas/DemoTo' == api.paths['/demo/v1/longs'].post.requestBody.content['application/json'].schema.items['$ref']
assert ['void', 'DemoTo'] == api.components.schemas.keySet() as List
assert '#/components/schemas/DemoTo' == api.components.schemas.DemoTo.properties.parent['$ref']
assert 'integer' == api.components.schemas.DemoTo.properties.id.schema?.type ?: api.components.schemas.DemoTo.properties.id.type

// the YAML template shares the info section
String yaml = new File(basedir, 'target/site/servicedoc/OpenApi.yaml').getText('UTF-8').replace('\r\n', '\n')
assert yaml.contains('  contact:\n    name: J\u00f6rg Hohwiller\n')
assert yaml.contains('  license:\n    name: Apache Software Licenese\n')

for (name in ['OpenApi.json', 'OpenApi.yaml']) {
    def text = new File(basedir, 'target/site/servicedoc/' + name).getText('UTF-8').replace('\r\n', '\n')
    assert !text.contains('$services') : "unrendered Velocity reference in ${name}"
}
