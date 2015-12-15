/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
File doc = new File( basedir, 'target/site/servicedoc/index.html' )
assert doc.exists()

def html = new XmlSlurper().parse( doc )

assert 'Demo Documentation' ==  html.body.div[0].h1.text()
assert 'This is a showcase, example and integration test of the plugin.' == html.body.div[0].div[0].p.text()
assert '1.0-SNAPSHOT' == html.body.div[0].div.div[0].span.text()
assert 'mailto:joerg.hohwiller@googlemail.com' == html.body.div[0].div.div[1].span.a.@href.text()
assert 'J\u00F6rg Hohwiller' == html.body.div[0].div.div[1].span.a.text()
assert 'http://www.apache.org/licenses/LICENSE-2.0.txt' == html.body.div[0].div.div[2].span.a.@href.text()
assert 'Apache Software Licenese' == html.body.div[0].div.div[2].span.a.text()
assert 'DemoRestService' == html.body.div[1].ul.li.div.h2.span[0].a.text()
assert 'This is a cool REST-Service to demonstrate servicedocgen-maven-plugin.' == html.body.div[1].ul.li.div.h2.span[1].a.text()

