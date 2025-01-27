/*
* Copyright (C) 2021, Alexei Khatskevich
*
* Licensed under the BSD 3-Clause license.
* You may obtain a copy of the License at
*
* https://github.com/Gmugra/net.cactusthorn.config/blob/main/LICENSE
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.cactusthorn.config.core.loader.standard;

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static net.cactusthorn.config.core.util.ApiMessages.msg;
import static net.cactusthorn.config.core.util.ApiMessages.Key.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.cactusthorn.config.core.loader.Loader;

public class ClasspathJarManifestLoader implements Loader {

    private static final Logger LOG = Logger.getLogger(ClasspathJarManifestLoader.class.getName());

    private static final String SUB_PREFIX = "jar:manifest?";
    private static final String PREFIX = "classpath:" + SUB_PREFIX;

    @Override public boolean accept(URI uri) {
        return uri.toString().startsWith(PREFIX);
    }

    @Override public Map<String, String> load(URI uri, ClassLoader classLoader) {

        String param = uri.getSchemeSpecificPart().substring(SUB_PREFIX.length());
        String[] parts = param.split("=", 2);
        String name = parts[0];
        String value = parts.length > 1 ? parts[1] : null;

        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream in = url.openStream()) {

                    Manifest manifest = new Manifest(in);
                    Attributes attributes = manifest.getMainAttributes();
                    String attribute = attributes.getValue(name);
                    if (attribute != null && (value == null || value.equals(attribute))) {
                        return attributes.entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
                    }
                }
            }
        } catch (IOException e) {
            LOG.info(msg(CANT_LOAD_RESOURCE, uri.toString(), e.toString()));
            return Collections.emptyMap();
        }

        if (value != null) {
            LOG.info(msg(MANIFEST_NOT_FOUND_1, name, value));
            return Collections.emptyMap();
        }
        LOG.info(msg(MANIFEST_NOT_FOUND_2, name));
        return Collections.emptyMap();
    }

}
