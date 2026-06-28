package com.hacc.api.domain.repository;

import java.util.List;
import java.util.Optional;

import com.hacc.api.domain.model.Propietario;

public interface IPropietarioRepo {
    public void crearPropietario(Propietario propietario);
    public Propietario obtenerPropietario(Integer id_propietario);   
    public void actualizarPropietario(Propietario propietario);
    public void eliminarPropietario(Integer id_propietario);
    public List<Propietario> listarPropietarios();
    public boolean existePorEmail(String email);
    public boolean existePorCedula(String cedula);
    Optional<Propietario> buscarPorId(Integer id);
    List<Propietario> listarTodos();
    void actualizar(Propietario propietario);

}