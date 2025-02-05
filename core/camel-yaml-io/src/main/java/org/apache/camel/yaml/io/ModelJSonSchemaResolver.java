/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.yaml.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.CatalogCamelContext;
import org.apache.camel.catalog.JSonSchemaResolver;
import org.apache.camel.spi.ClassResolver;
import org.apache.camel.support.CamelContextHelper;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;

/**
 * To load model json metadata
 */
class ModelJSonSchemaResolver implements JSonSchemaResolver {

    private CatalogCamelContext camelContext;
    private ClassLoader classLoader;

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = (CatalogCamelContext) camelContext;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String getComponentJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getComponentParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getDataFormatJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getDataFormatParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getLanguageJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getLanguageParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getTransformerJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getTransformerParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getDevConsoleJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getDevConsoleParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getOtherJSonSchema(String name) {
        // not supported
        return null;
    }

    @Override
    public String getPojoBeanJSonSchema(String name) {
        if (camelContext != null) {
            try {
                return camelContext.getPojoBeanParameterJsonSchema(name);
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public String getModelJSonSchema(String name) {
        try {
            String[] subPackages = new String[] {
                    "", "cloud/", "config/", "dataformat/", "errorhandler/", "language/", "loadbalancer/", "rest/",
                    "transformer/", "validator/" };
            for (String sub : subPackages) {
                String path = CamelContextHelper.MODEL_DOCUMENTATION_PREFIX + sub + name + ".json";
                String data = doLoadResource(path);
                if (data != null) {
                    return data;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    @Override
    public String getMainJsonSchema() {
        try {
            String path = "META-INF/camel-main-configuration-metadata.json";
            return doLoadResource(path);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private String doLoadResource(String path) throws IOException {
        InputStream is = null;
        if (classLoader != null) {
            is = classLoader.getResourceAsStream(path);
        }
        if (is == null && camelContext != null) {
            ClassResolver resolver = camelContext.getClassResolver();
            is = resolver.loadResourceAsStream(path);
        }
        if (is == null) {
            is = ObjectHelper.loadResourceAsStream(path, Thread.currentThread().getContextClassLoader());
        }
        if (is != null) {
            try {
                return IOHelper.loadText(is);
            } catch (IOException e) {
                // ignore
            } finally {
                IOHelper.close(is);
            }
        }
        return null;
    }

}
