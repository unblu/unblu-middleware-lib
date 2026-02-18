package com.unblu.middleware.config;

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
        ObjectMapper mapper = objectMapper.copy();
        mapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.KEBAB_CASE);
        return mapper.convertValue(nested, type);
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
        if (o instanceof List<?> list) return (List<Object>) list;
        throw new IllegalArgumentException("Expected List but got: " + o.getClass());
    }

    private static void ensureSize(List<Object> list, int size) {
        while (list.size() < size) {
            list.add(null);
        }
    }

    enum TokenKind { PROP, INDEX }

    static class Token {
        TokenKind kind;
        String name;    // for PROP
        int index;      // for INDEX

        static List<Token> parse(String key) {
            List<Token> result = new ArrayList<>();
            int i = 0;
            while (i < key.length()) {
                if (key.charAt(i) == '[') {
                    int close = key.indexOf(']', i);
                    if (close == -1) throw new IllegalArgumentException("Unclosed [: " + key);
                    String num = key.substring(i + 1, close);
                    Token t = new Token();
                    t.kind = TokenKind.INDEX;
                    t.index = Integer.parseInt(num);
                    result.add(t);
                    i = close + 1;
                    if (i < key.length() && key.charAt(i) == '.') i++; // skip trailing dot
                } else {
                    int next = indexOfAny(key, i, '[', '.');
                    if (next == -1) next = key.length();
                    String name = key.substring(i, next);
                    if (!name.isEmpty()) {
                        Token t = new Token();
                        t.kind = TokenKind.PROP;
                        t.name = name;
                        result.add(t);
                    }
                    i = next;
                    if (i < key.length() && key.charAt(i) == '.') i++;
                }
            }
            return result;
        }

        private static int indexOfAny(String s, int start, char... chars) {
            for (int i = start; i < s.length(); i++) {
                for (char c : chars) {
                    if (s.charAt(i) == c) return i;
                }
            }
            return -1;
        }
    }
}

