package com.supplyhub.ms_productos.service;

import com.supplyhub.ms_productos.client.CategoriaClient;
import com.supplyhub.ms_productos.client.ProveedorClient;
import com.supplyhub.ms_productos.dto.CategoriaDTO;
import com.supplyhub.ms_productos.dto.ProductoRequestDTO;
import com.supplyhub.ms_productos.dto.ProductoResponseDTO;
import com.supplyhub.ms_productos.dto.ProveedorDTO;
import com.supplyhub.ms_productos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_productos.model.Producto;
import com.supplyhub.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private CategoriaClient categoriaClient;

    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private ProductoService service;

    private CategoriaDTO categoriaActiva() {
        return new CategoriaDTO(1L, "Herramientas", "Herramientas manuales", "ACTIVO");
    }

    private ProveedorDTO proveedorActivo() {
        return new ProveedorDTO(1L, "76.987.654-3", "Proveedor Test", "prov@test.com",
                "+56987654321", "Av. Industrial", "ACTIVO");
    }

    private ProductoRequestDTO dtoValido() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Martillo");
        dto.setDescripcion("Martillo de acero");
        dto.setPrecio(15000);
        dto.setIdCategoria(1L);
        dto.setIdProveedor(1L);
        dto.setEstado("ACTIVO");
        return dto;
    }

    @Test
    void deberiaRetornarProductoCuandoExiste() {
        Producto producto = new Producto(1L, "Martillo", "Martillo de acero", 15000, 1L, 1L, "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(proveedorActivo());

        ProductoResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals("Martillo", resultado.getNombre());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaProductos() {
        Producto producto = new Producto(1L, "Martillo", "Martillo de acero", 15000, 1L, 1L, "ACTIVO");
        when(repository.findAll()).thenReturn(List.of(producto));
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(proveedorActivo());

        List<ProductoResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("Martillo", resultado.get(0).getNombre());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearProductoCorrectamente() {
        ProductoRequestDTO dto = dtoValido();
        when(repository.findByNombreIgnoreCase("Martillo")).thenReturn(Optional.empty());
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(proveedorActivo());
        when(repository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductoResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals("Martillo", resultado.getNombre());
        verify(repository).save(any(Producto.class));
    }

    @Test
    void deberiaRechazarProductoConNombreDuplicado() {
        ProductoRequestDTO dto = dtoValido();
        when(repository.findByNombreIgnoreCase("Martillo"))
                .thenReturn(Optional.of(new Producto(1L, "Martillo", "Duplicado", 15000, 1L, 1L, "ACTIVO")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    void deberiaRechazarCategoriaInactiva() {
        ProductoRequestDTO dto = dtoValido();
        CategoriaDTO inactiva = categoriaActiva();
        inactiva.setEstado("INACTIVO");

        when(repository.findByNombreIgnoreCase("Martillo")).thenReturn(Optional.empty());
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(inactiva);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("categoría inactiva"));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    void deberiaRechazarProveedorInactivo() {
        ProductoRequestDTO dto = dtoValido();
        ProveedorDTO inactivo = proveedorActivo();
        inactivo.setEstado("INACTIVO");

        when(repository.findByNombreIgnoreCase("Martillo")).thenReturn(Optional.empty());
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(inactivo);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("proveedor inactivo"));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    void deberiaRechazarEstadoInvalido() {
        ProductoRequestDTO dto = dtoValido();
        dto.setEstado("BORRADOR");

        when(repository.findByNombreIgnoreCase("Martillo")).thenReturn(Optional.empty());
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(proveedorActivo());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("ACTIVO o INACTIVO"));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    void deberiaActualizarProductoCorrectamente() {
        Producto existente = new Producto(1L, "Nombre viejo", "Desc", 10000, 1L, 1L, "ACTIVO");
        ProductoRequestDTO dto = dtoValido();
        dto.setNombre("Nombre nuevo");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByNombreIgnoreCase("Nombre nuevo")).thenReturn(Optional.of(existente));
        when(categoriaClient.obtenerCategoria(1L, null)).thenReturn(categoriaActiva());
        when(proveedorClient.obtenerProveedor(1L, null)).thenReturn(proveedorActivo());
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("Nombre nuevo", resultado.getNombre());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarProductoPorId() {
        Producto producto = new Producto(1L, "Eliminar", "Desc", 10000, 1L, 1L, "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(producto);
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarProductoInexistente() {
        ProductoRequestDTO dto = dtoValido();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizar(99L, dto, null));
        verify(repository).findById(99L);
        verify(repository, never()).save(any(Producto.class));
    }
}
