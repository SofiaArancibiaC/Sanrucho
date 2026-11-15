package cl.sanrucho.common.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Blacklist de tokens JWT en memoria.
 *
 * Cuando un usuario hace logout, su token se agrega aqui y el filtro JWT
 * lo rechaza aunque aun no haya expirado. Los tokens expirados se limpian
 * automaticamente cada 10 minutos.
 *
 * NOTA: en un entorno con multiples instancias se deberia usar Redis u otro
 * almacen compartido. Para el alcance de esta evaluacion es suficiente.
 */
@Slf4j
@Service
@EnableScheduling
public class TokenBlacklistService {

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void agregarToken(String token, Date expiracion) {
        if (token != null && expiracion != null) {
            blacklist.put(token, expiracion);
            log.debug("Token agregado a la blacklist. Total: {}", blacklist.size());
        }
    }

    public boolean isBlacklisted(String token) {
        return token != null && blacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 600_000)
    public void limpiarTokensExpirados() {
        Date ahora = new Date();
        int antes = blacklist.size();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(ahora));
        int eliminados = antes - blacklist.size();
        if (eliminados > 0) {
            log.debug("Blacklist limpiada: {} tokens expirados eliminados", eliminados);
        }
    }
}
