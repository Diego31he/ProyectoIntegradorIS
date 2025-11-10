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
    @Transactional
    public Equipo guardar(Equipo e) {
        return equipoRepo.save(e);
    }

    @Transactional(readOnly = true)
    public Integer proximoNumero() {
        return equipoRepo.findTopByOrderByNumeroDesc()
                .map(eq -> eq.getNumero() + 1)
                .orElse(1);
    }

    @Transactional
    public void deleteEquipo(Long equipoId) {
        Equipo eq = equipoRepo.findById(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado id=" + equipoId));

        // 1. Desasignar a todos los integrantes
        if (eq.getIntegrantes() != null) {
            for (Estudiante integrante : eq.getIntegrantes()) {
                integrante.setEquipo(null);
                estudianteRepo.save(integrante);
            }
        }

        // 2. Borrar el equipo (ahora que no tiene FK)
        equipoRepo.delete(eq);
    }

    @Transactional(readOnly = true)
    public List<Estudiante> integrantesDe(Long equipoId) {
        return estudianteRepo.findByEquipo_IdOrderByApellidoAscNombreAsc(equipoId);
    }
    @Transactional(readOnly = true)
    public List<Equipo> findAll() {
        return equipoRepo.findAll();
    }

    @Transactional
    public Equipo crearEquipoSiguiente() {
        // Buscamos el número más alto actual
        Integer next = equipoRepo.findTopByOrderByNumeroDesc()
                .map(Equipo::getNumero)
                .map(n -> n + 1)
                .orElse(1); // Si no hay equipos, empieza en 1

        Equipo nuevo = new Equipo();
        nuevo.setNumero(next);
        nuevo.setNombre("Equipo " + next); // Nombre por defecto
        return equipoRepo.save(nuevo);
    }

    /**
     * Asigna un estudiante a un equipo.
     */
    @Transactional
    public void asignarEstudianteAEquipo(Long estudianteId, Long equipoId) {
        Estudiante e = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado id=" + estudianteId));
        Equipo eq = equipoRepo.findById(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado id=" + equipoId));
        e.setEquipo(eq);
        estudianteRepo.save(e);
    }

    /**
     * Quita al estudiante de cualquier equipo.
     */
    @Transactional
    public void quitarEquipoDeEstudiante(Long estudianteId) {
        Estudiante e = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado id=" + estudianteId));
        e.setEquipo(null);
        estudianteRepo.save(e);
    }

    /**
     * Devuelve una lista de todos los estudiantes que NO tienen equipo asignado.
     */
    @Transactional(readOnly = true)
    public List<Estudiante> findEstudiantesSinEquipo() {
        return estudianteRepo.findByEquipoIsNull();
    }
}
