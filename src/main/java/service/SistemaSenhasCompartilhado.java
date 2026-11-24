
package service;

import model.Senha;
import java.util.LinkedHashMap;
import java.util.Map;

public class SistemaSenhasCompartilhado {

    private static final SistemaSenhas sistema =
            new SistemaSenhas();

    private static final Map<Senha, String> dadosFila =
            new LinkedHashMap<>();

    public static SistemaSenhas getInstancia() {
        return sistema;
    }

    public static Map<Senha, String> getDadosFila() {
        return dadosFila;
    }
}
