import org.junit.Test;
import static org.junit.Assert.*;

package com.arka.arkavalenzuela.model;


public class SupplierTest {

    @Test
    public void testProveedorIdGetterAndSetter() {
        Supplier supplier = new Supplier();
        Long id = 123L;
        supplier.setProveedorId(id);
        assertEquals(id, supplier.getProveedorId());
    }

    @Test
    public void testNombreGetterAndSetter() {
        Supplier supplier = new Supplier();
        String nombre = "Proveedor Uno";
        supplier.setNombre(nombre);
        assertEquals(nombre, supplier.getNombre());
    }

    @Test
    public void testEmailContactoGetterAndSetter() {
        Supplier supplier = new Supplier();
        String email = "contacto@proveedor.com";
        supplier.setEmailContacto(email);
        assertEquals(email, supplier.getEmailContacto());
    }

    @Test
    public void testTelefonoGetterAndSetter() {
        Supplier supplier = new Supplier();
        String telefono = "123456789";
        supplier.setTelefono(telefono);
        assertEquals(telefono, supplier.getTelefono());
    }

    @Test
    public void testPaisGetterAndSetter() {
        Supplier supplier = new Supplier();
        String pais = "Chile";
        supplier.setPais(pais);
        assertEquals(pais, supplier.getPais());
    }
}