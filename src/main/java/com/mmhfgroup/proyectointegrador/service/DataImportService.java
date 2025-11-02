package com.mmhfgroup.proyectointegrador.service;

import com.mmhfgroup.proyectointegrador.model.Catedra;
import com.mmhfgroup.proyectointegrador.model.Estudiante;
import com.mmhfgroup.proyectointegrador.model.Proyecto;
import com.mmhfgroup.proyectointegrador.repository.CatedraRepository; // (Necesitarás crear este repo)
import com.mmhfgroup.proyectointegrador.repository.EstudianteRepository; // (Necesitarás crear este repo)
import com.mmhfgroup.proyectointegrador.repository.ProyectoRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class DataImportService {

    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private CatedraRepository catedraRepository;

    public void importarDesdeStream(InputStream inputStream) {

        System.out.println("Iniciando importación de datos desde el stream...");

        // Usamos el InputStream que nos pasa el admin
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Itera sobre las filas, saltando la primera (cabecera)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // ... (EXACTAMENTE la misma lógica de lectura que tenías antes)
                // Row row = sheet.getRow(i);
                // ...
                // Proyecto p = new Proyecto();
                // ...
                // proyectoRepository.save(p);
            }

            System.out.println("Importación finalizada con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fatal al leer el archivo Excel.");
            // Aquí podrías lanzar una excepción para notificar al admin
        }
    }
}