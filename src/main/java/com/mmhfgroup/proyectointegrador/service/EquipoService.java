package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Equipo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquipoService {

    // Lista estática compartida entre todas las vistas
    private static final List<Equipo> equipos = new ArrayList<>();
    private static int contador = 1;

    // Devuelve una vista de solo lectura
    public List<Equipo> listarEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public void agregarEquipo(String nombre, String auditor) {
        Equipo nuevo = new Equipo(contador++, nombre, auditor);
        equipos.add(nuevo);
    }

    public void eliminarEquipo(Equipo equipo) {
        equipos.remove(equipo);
    }

    // Método opcional para limpiar todo (por ejemplo, al cerrar sesión)
    public void limpiarEquipos() {
        equipos.clear();
        contador = 1;
    }
}
