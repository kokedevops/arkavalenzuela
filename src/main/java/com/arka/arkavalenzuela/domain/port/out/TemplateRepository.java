package com.arka.arkavalenzuela.domain.port.out;

import java.util.Optional;

/**
 * Puerto de salida que define la interfaz para obtener el contenido de las plantillas.
 * El dominio interactúa con esta interfaz sin conocer los detalles de la infraestructura.
 */
public interface TemplateRepository {
    /**
     * Obtiene el contenido de una plantilla por su nombre.
     *
     * @param templateName El nombre de la plantilla (ej. "bienvenida.html").
     * @return Un Optional que contiene el contenido de la plantilla si se encuentra, o vacío si no.
     */
    Optional<String> getTemplateContent(String templateName);
}