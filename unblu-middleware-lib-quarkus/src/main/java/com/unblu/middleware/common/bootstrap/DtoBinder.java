package com.unblu.middleware.common.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class DtoBinder {

    @Inject ObjectMapper objectMapper;

    public <T> T bindPrefix(SmallRyeConfig cfg, String prefix, Class<T> type) {
        Map<String, String> flat = readFlatSubtree(cfg, prefix);
        Object nested = expand(flat); // Map/List scalar tree
        return objectMapper.convertValue(nested, type);
    }

    private Map<String, String> readFlatSubtree(SmallRyeConfig cfg, String prefix) {
        String p = prefix.endsWith(".") ? prefix : prefix + ".";
        Map<String, String> out = new HashMap<>();

        for (String name : cfg.getPropertyNames()) {
            if (name.equals(prefix)) {
                out.put("", cfg.getRawValue(name));
            } else if (name.startsWith(p)) {
                String subKey = name.substring(p.length());
                out.put(subKey, cfg.getRawValue(name));
            }
        }
        return out;
    }

    /** Expand "a.b[0].c" keys into nested Maps/Lists of scalars. */
    @SuppressWarnings("unchecked")
    static Object expand(Map<String, String> flat) {
        Map<String, Object> root = new LinkedHashMap<>();

        for (var e : flat.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            if (key == null || key.isBlank()) {
                // Edge case: prefix itself has a scalar. Usually you won't use this.
                root.put("", value);
                continue;
            }

            List<Token> tokens = Token.parse(key);
            Object current = root;

            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                boolean last = (i == tokens.size() - 1);

                if (t.kind == TokenKind.PROP) {
                    Map<String, Object> m = asMap(current);

                    if (last) {
                        m.put(t.name, value);
                    } else {
                        Object next = m.get(t.name);
                        Token nextTok = tokens.get(i + 1);

                        if (next == null) {
                            next = (nextTok.kind == TokenKind.INDEX) ? new ArrayList<>() : new LinkedHashMap<String, Object>();
                            m.put(t.name, next);
                        }
                        current = next;
                    }
                } else { // INDEX
                    List<Object> list = asList(current);
                    ensureSize(list, t.index + 1);

                    if (last) {
                        list.set(t.index, value);
                    } else {
                        Object next = list.get(t.index);
                        Token nextTok = tokens.get(i + 1);

                        if (next == null) {
                            next = (nextTok.kind == TokenKind.INDEX) ? new ArrayList<>() : new LinkedHashMap<String, Object>();
                            list.set(t.index, next);
                        }
                        current = next;
                    }
                }
            }
        }

        return root;
    }

    private static Map<String, Object> asMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<String, Object>) m;
        throw new IllegalArgumentException("Expected Map but got: " + o.getClass());
    }

    private static List<Object> asList(Object o) {
        if (o instanceof List<?> l) return (List<Object>) l;
        throw new IllegalArgumentException("Expected List but got: " + o.getClass());
    }

    private static void ensureSize(List<Object> list, int size) {
        while (list.size() < size) list.add(null);
    }

    enum TokenKind { PROP, INDEX }

    static final class Token {
        final TokenKind kind;
        final String name;
        final int index;

        private Token(TokenKind kind, String name, int index) {
            this.kind = kind; this.name = name; this.index = index;
        }

        static Token prop(String name) { return new Token(TokenKind.PROP, name, -1); }
        static Token idx(int index) { return new Token(TokenKind.INDEX, null, index); }

        // Parses "a.b[0].c" into [PROP(a), PROP(b), INDEX(0), PROP(c)]
        static List<Token> parse(String key) {
            List<Token> out = new ArrayList<>();
            int i = 0;
            StringBuilder buf = new StringBuilder();

            while (i < key.length()) {
                char ch = key.charAt(i);

                if (ch == '.') {
                    if (buf.length() > 0) { out.add(prop(buf.toString())); buf.setLength(0); }
                    i++;
                    continue;
                }

                if (ch == '[') {
                    if (buf.length() > 0) { out.add(prop(buf.toString())); buf.setLength(0); }
                    int end = key.indexOf(']', i);
                    if (end < 0) throw new IllegalArgumentException("Missing ] in: " + key);
                    int idx = Integer.parseInt(key.substring(i + 1, end));
                    out.add(idx(idx));
                    i = end + 1;
                    continue;
                }

                buf.append(ch);
                i++;
            }

            if (buf.length() > 0) out.add(prop(buf.toString()));
            return out;
        }
    }
}
