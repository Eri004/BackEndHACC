package com.hacc.api.domain.repository;

import java.util.List;
import java.util.Optional;

import com.hacc.api.domain.model.Residente;

public interface IResidenteRepo {

    public Residente crearResidente(Residente residente);
    public Residente obtenerResidente(Integer id_residente);
    public void actualizarResidente(Residente residente);
    public void eliminarResidente(Integer id_residente);
    public List<Residente> listarResidentes();

    public Optional<Residente> buscarPorId(Integer idResidente);
    public List<Residente> listarActivos();
    public long contarActivos();
    
}
