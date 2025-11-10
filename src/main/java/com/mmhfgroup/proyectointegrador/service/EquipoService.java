package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Equipo;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.repository.EquipoRepository;
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepo;
    private final EstudianteRepository estudianteRepo;

    public EquipoService(EquipoRepository equipoRepo, EstudianteRepository estudianteRepo) {
        this.equipoRepo = equipoRepo;
        this.estudianteRepo = estudianteRepo;
    }

    @Transactional(readOnly = true)
    public List<Equipo> listarEquipos() {
        return equipoRepo.findAllByOrderByNumeroAsc();
    }

    @Transactional
    public Equipo crearEquipo(Integer numero, String nombre, String auditor) {
        Equipo e = new Equipo(numero, nombre, auditor);
        return equipoRepo.save(e);
    }

    @Transactional
    public void eliminarEquipo(Long id) {
        equipoRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Integer proximoNumero() {
        return equipoRepo.findTopByOrderByNumeroDesc()
                .map(eq -> eq.getNumero() + 1)
                .orElse(1);
    }

    @Transactional(readOnly = true)
    public List<Estudiante> integrantesDe(Long equipoId) {
        return estudianteRepo.findByEquipo_IdOrderByApellidoAscNombreAsc(equipoId);
    }
}
