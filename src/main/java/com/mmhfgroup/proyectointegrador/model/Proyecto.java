package com.mmhfgroup.proyectointegrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue
    private Long id;

    // --- Relaciones ---

    // Un proyecto puede tener varios estudiantes (un equipo)
    @ManyToMany
    private List<Estudiante> estudiantes;

    // Un proyecto tiene un director funcional (que es de Cátedra)
    @ManyToOne
    private Catedra directorFuncional;

    // Un proyecto tiene un director técnico (que es de Cátedra)
    @ManyToOne
    private Catedra directorTecnico;

    // --- Datos del Excel ---

    private String titulo; // "Proyecto"
    private String beneficiarios;
    private String codigoFIM;
    private Integer equipo; // "Equipo"

    private Year ingreso; // "Ingreso"
    private Integer anioCurso;
    private Year anioVence;
    private String avance; // "Avance"
    private Integer cohorte;

    private LocalDate fechaPresentacion;
    private LocalDate fechaActa;
    private Double nota;

    // --- Constructores, Getters y Setters ---

    public Proyecto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public Catedra getDirectorFuncional() {
        return directorFuncional;
    }

    public void setDirectorFuncional(Catedra directorFuncional) {
        this.directorFuncional = directorFuncional;
    }

    public Catedra getDirectorTecnico() {
        return directorTecnico;
    }

    public void setDirectorTecnico(Catedra directorTecnico) {
        this.directorTecnico = directorTecnico;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(String beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public String getCodigoFIM() {
        return codigoFIM;
    }

    public void setCodigoFIM(String codigoFIM) {
        this.codigoFIM = codigoFIM;
    }

    public Integer getEquipo() {
        return equipo;
    }

    public void setEquipo(Integer equipo) {
        this.equipo = equipo;
    }

    public Year getIngreso() {
        return ingreso;
    }

    public void setIngreso(Year ingreso) {
        this.ingreso = ingreso;
    }

    public Integer getAnioCurso() {
        return anioCurso;
    }

    public void setAnioCurso(Integer anioCurso) {
        this.anioCurso = anioCurso;
    }

    public Year getAnioVence() {
        return anioVence;
    }

    public void setAnioVence(Year anioVence) {
        this.anioVence = anioVence;
    }

    public String getAvance() {
        return avance;
    }

    public void setAvance(String avance) {
        this.avance = avance;
    }

    public Integer getCohorte() {
        return cohorte;
    }

    public void setCohorte(Integer cohorte) {
        this.cohorte = cohorte;
    }

    public LocalDate getFechaPresentacion() {
        return fechaPresentacion;
    }

    public void setFechaPresentacion(LocalDate fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public LocalDate getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(LocalDate fechaActa) {
        this.fechaActa = fechaActa;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
}