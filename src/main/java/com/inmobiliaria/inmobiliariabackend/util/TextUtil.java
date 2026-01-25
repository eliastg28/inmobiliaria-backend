package com.inmobiliaria.inmobiliariabackend.util;

import java.text.Normalizer;

public class TextUtil {

    public static String limpiarAcentos(String texto) {
        if (texto == null) return null;
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
}