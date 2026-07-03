package com.haccphoenix.api.startup;

import com.haccphoenix.api.domain.model.TipoCargo;
import com.haccphoenix.api.domain.model.TipoGasto;
import com.haccphoenix.api.domain.repository.TipoCargoRepository;
import com.haccphoenix.api.domain.repository.TipoGastoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class StartupDataInitializer {

    private static final Logger LOG = Logger.getLogger(StartupDataInitializer.class);

    @Inject
    TipoCargoRepository tipoCargoRepository;

    @Inject
    TipoGastoRepository tipoGastoRepository;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        seedTipoCargos();
        seedTipoGastos();
    }

    private void seedTipoCargos() {
        if (tipoCargoRepository.count() > 0) {
            LOG.debug("Tipos de cargo ya cargados, omitiendo seed.");
            return;
        }
        LOG.info("Insertando tipos de cargo por defecto...");
        List<TipoCargo> defaults = List.of(
            build("ALICUOTA", "Cuota ordinaria mensual del departamento"),
            build("MULTA", "Multa aplicada al departamento"),
            build("INTERES_MORA", "Interes por mora en el pago"),
            build("CUOTA_EXTRAORDINARIA", "Cuota extraordinaria aprobada por asamblea"),
            build("OTRO", "Otro tipo de cargo")
        );
        for (TipoCargo t : defaults) {
            tipoCargoRepository.persist(t);
        }
        LOG.infof("Insertados %d tipos de cargo.", defaults.size());
    }

    private void seedTipoGastos() {
        if (tipoGastoRepository.count() > 0) {
            LOG.debug("Tipos de gasto ya cargados, omitiendo seed.");
            return;
        }
        LOG.info("Insertando tipos de gasto por defecto...");
        List<TipoGasto> defaults = List.of(
            buildG("SERVICIOS_BASICOS", "Agua, luz, gas, internet"),
            buildG("MANTENIMIENTO", "Mantenimiento general del edificio"),
            buildG("LIMPIEZA", "Servicios de limpieza"),
            buildG("SEGURIDAD", "Servicios de seguridad y vigilancia"),
            buildG("REPARACIONES", "Reparaciones locativas"),
            buildG("ADMINISTRACION", "Gastos administrativos"),
            buildG("OTRO", "Otro tipo de gasto")
        );
        for (TipoGasto t : defaults) {
            tipoGastoRepository.persist(t);
        }
        LOG.infof("Insertados %d tipos de gasto.", defaults.size());
    }

    private TipoCargo build(String nombre, String descripcion) {
        TipoCargo t = new TipoCargo();
        t.setNombre(nombre);
        t.setDescripcion(descripcion);
        t.setActivo(true);
        return t;
    }

    private TipoGasto buildG(String nombre, String descripcion) {
        TipoGasto t = new TipoGasto();
        t.setNombre(nombre);
        t.setDescripcion(descripcion);
        t.setActivo(true);
        return t;
    }
}
