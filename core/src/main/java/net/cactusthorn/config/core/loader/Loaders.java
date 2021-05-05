package net.cactusthorn.config.core.loader;

import static net.cactusthorn.config.core.ApiMessages.msg;
import static net.cactusthorn.config.core.ApiMessages.Key.LOADER_NOT_FOUND;

import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.cactusthorn.config.core.util.VariablesParser;

public final class Loaders {

    public static final class UriTemplate {
        private URI uri;
        private String template;
        private boolean variable = false;
        private boolean cachable = true;

        public UriTemplate(URI uri, boolean cachable) {
            this.uri = replaceUserHome(uri);
            this.cachable = cachable;
        }

        public UriTemplate(String template, boolean cachable) {
            this.template = replaceUserHome(template);
            this.cachable = cachable;
            if (template.indexOf("{") != -1) {
                variable = true;
            } else {
                uri = URI.create(this.template);
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" }) private URI uri() {
            if (!variable) {
                return uri;
            }
            Map<String, String> values = new HashMap<>(System.getenv());
            values.putAll((Map) System.getProperties());
            return URI.create(new VariablesParser(template).replace(values));
        }

        private boolean cachable() {
            return cachable;
        }

        private static final String USERHOME_PREFIX = "file:~/";

        private URI replaceUserHome(URI u) {
            String tmp = replaceUserHome(u.toString());
            return URI.create(tmp);
        }

        private String replaceUserHome(String str) {
            if (str.indexOf(USERHOME_PREFIX) == -1) {
                return str;
            }
            String userHome = userHome().toString();
            return str.replace(USERHOME_PREFIX, userHome);
        }

        private static final String USER_HOME = "user.home";

        private URI userHome() {
            return java.nio.file.Paths.get(System.getProperty(USER_HOME)).toUri();
        }
    }

    private final ConcurrentHashMap<URI, Map<String, String>> cache = new ConcurrentHashMap<>();

    private final LoadStrategy loadStrategy;
    private final LinkedHashSet<UriTemplate> templates;
    private final Deque<Loader> loaders;
    private final Map<String, String> properties;

    public Loaders(LoadStrategy loadStrategy, LinkedHashSet<UriTemplate> templates, Deque<Loader> loaders, Map<String, String> properties) {
        this.loadStrategy = loadStrategy;
        this.templates = templates;
        this.loaders = loaders;
        this.properties = properties;
    }

    public Map<String, String> load(ClassLoader classLoader) {
        List<Map<String, String>> values = new ArrayList<>();
        for (UriTemplate template : templates) {
            URI uri = template.uri();
            Loader loader = loaders.stream().filter(l -> l.accept(uri)).findFirst()
                    .orElseThrow(() -> new UnsupportedOperationException(msg(LOADER_NOT_FOUND, uri)));
            Map<String, String> uriProperties;
            if (template.cachable()) {
                uriProperties = cache.computeIfAbsent(uri, u -> loader.load(u, classLoader));
            } else {
                uriProperties = loader.load(uri, classLoader);
            }
            values.add(uriProperties);
        }
        return loadStrategy.combine(values, properties);
    }
}