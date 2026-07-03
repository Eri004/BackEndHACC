package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Edificio;
import com.haccphoenix.api.domain.model.Gasto;
import com.haccphoenix.api.domain.model.TipoGasto;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.EdificioRepository;
import com.haccphoenix.api.domain.repository.GastoRepository;
import com.haccphoenix.api.domain.repository.TipoGastoRepository;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class GastoService {

    @Inject
    GastoRepository gastoRepository;

    @Inject
    EdificioRepository edificioRepository;

    @Inject
    TipoGastoRepository tipoGastoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public Gasto registrar(Gasto gasto, Integer edificioId, Integer tipoGastoId, Integer usuarioId) {
        Edificio edificio = edificioRepository.findById(edificioId);
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + edificioId);
        }
        TipoGasto tipoGasto = tipoGastoRepository.findById(tipoGastoId);
        if (tipoGasto == null) {
            throw new RuntimeException("Tipo de gasto no encontrado con ID: " + tipoGastoId);
        }
        validar(gasto);
        gasto.setEdificio(edificio);
        gasto.setTipoGasto(tipoGasto);

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId);
            gasto.setUsuario(usuario);
        }

        gastoRepository.persist(gasto);
        return gasto;
    }

    public Gasto obtener(Integer id) {
        Gasto gasto = gastoRepository.findById(id);
        if (gasto == null) {
            throw new RuntimeException("Gasto no encontrado con ID: " + id);
        }
        return gasto;
    }

    public List<Gasto> listar() {
        return gastoRepository.listAll();
    }

    public List<Gasto> listarPorEdificio(Integer edificioId) {
        return gastoRepository.listarPorEdificio(edificioId);
    }

    public List<Gasto> listarPorTipoGasto(Integer tipoGastoId) {
        return gastoRepository.listarPorTipoGasto(tipoGastoId);
    }

    public List<Gasto> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return gastoRepository.listarPorPeriodo(inicio, fin);
    }

    @Transactional
    public Gasto actualizar(Integer id, Gasto dto) {
        Gasto existente = obtener(id);

        existente.setFecha(dto.getFecha());
        existente.setDescripcion(dto.getDescripcion());
        existente.setValor(dto.getValor());
        existente.setComprobante(dto.getComprobante());

        if (dto.getTipoGasto() != null && dto.getTipoGasto().getId() != null) {
            TipoGasto tipoGasto = tipoGastoRepository.findById(dto.getTipoGasto().getId());
            existente.setTipoGasto(tipoGasto);
        }

        gastoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Gasto gasto = obtener(id);
        gastoRepository.delete(gasto);
    }

    private void validar(Gasto gasto) {
        if (gasto == null) {
            throw new RuntimeException("Los datos del gasto son obligatorios");
        }
        if (gasto.getFecha() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }
        if (gasto.getDescripcion() == null || gasto.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripcion es obligatoria");
        }
        if (gasto.getValor() == null) {
            throw new RuntimeException("El valor es obligatorio");
        }
    }
}
