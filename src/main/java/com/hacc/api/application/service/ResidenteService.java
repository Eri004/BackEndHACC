package com.hacc.api.application.service;

import java.util.List;

import com.hacc.api.domain.model.Residente;
import com.hacc.api.domain.repository.IResidenteRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ResidenteService {

    @Inject
    IResidenteRepo residenteRepo;

    public void crear(Residente residente) {
        residenteRepo.crearResidente(residente);
    }

    public Residente obtener(Integer id_residente) {
        return residenteRepo.obtenerResidente(id_residente);
    }

    public void actualizar(Integer id_residente, Residente residente) {

        Residente residenteExistente = residenteRepo.obtenerResidente(id_residente);
        residenteExistente.setNombre(residente.getNombre());
        residenteExistente.setApellido(residente.getApellido());
        residenteExistente.setDepartamento(residente.getDepartamento());
        residenteExistente.setTelefono(residente.getTelefono());
        residenteExistente.setUltimoPago(residente.getUltimoPago());
        residenteExistente.setDeuda(residente.getDeuda());
        residenteExistente.setEstado(residente.getEstado());
        residenteExistente.setEmail(residente.getEmail());
residenteExistente.setCedula(residente.getCedula());
residenteExistente.setContrasena(residente.getContrasena());

        residenteRepo.actualizarResidente(residenteExistente);
    }

    public void eliminar(Integer id_residente) {
        residenteRepo.eliminarResidente(id_residente);
    }

    public List<Residente> listar() {
        return residenteRepo.listarResidentes();
    }

}
