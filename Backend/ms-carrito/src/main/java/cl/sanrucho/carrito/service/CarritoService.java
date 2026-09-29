package cl.sanrucho.carrito.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.carrito.client.CatalogoClient;
import cl.sanrucho.carrito.dto.AgregarItemRequest;
import cl.sanrucho.carrito.dto.CarritoRequest;
import cl.sanrucho.carrito.dto.CarritoResponse;
import cl.sanrucho.carrito.dto.ProductoResponse;
import cl.sanrucho.carrito.exception.ProductoNoDisponibleException;
import cl.sanrucho.carrito.mapper.CarritoMapper;
import cl.sanrucho.carrito.mapper.ItemCarritoMapper;
import cl.sanrucho.carrito.model.entity.Carrito;
import cl.sanrucho.carrito.model.entity.ItemCarrito;
import cl.sanrucho.carrito.repository.CarritoRepository;
import cl.sanrucho.carrito.repository.ItemCarritoRepository;
import cl.sanrucho.common.exception.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CarritoMapper carritoMapper;
    private final ItemCarritoMapper ItemCarritoMapper;
    private final CatalogoClient catalogoClient;
    
    public CarritoResponse crearCarrito(CarritoRequest request){
        if (carritoRepository.existsById(request.getUsuarioId())){
            throw new DuplicateResourceException("Carrito", "UsuarioId", request.getUsuarioId(), "El cliente ya tiene un carrito activo.");
        }
        Carrito carrito = carritoMapper.toEntity(request);
        carritoRepository.save(carrito);
        return carritoMapper.toResponse(carrito);
    }

     public CarritoResponse obtenerCarritoPorCliente(Integer clienteId) {
        Carrito carrito = carritoRepository.findByUsuarioId(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito", "ID", clienteId));
        return carritoMapper.toResponse(carrito);
    }

    public CarritoResponse obtenerCarritoPorId(Integer usuarioId){
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElseThrow(()-> new EntityNotFoundException("Carrito", "ID", usuarioId));
        return carritoMapper.toResponse(carrito);
    }

    @Transactional
    public CarritoResponse agregarItem(Integer idCarrito, AgregarItemRequest request){
        // Cliente de catalogo
        ProductoResponse producto = catalogoClient.getProductoById(request.getIdProducto());
        if (producto == null){
           throw new EntityNotFoundException("Producto", "ID", request.getIdProducto());
        }

        Carrito carrito = carritoRepository.findByUsuarioId(idCarrito).orElseThrow(()-> new EntityNotFoundException("Carrito", "ID", idCarrito));

        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), request.getIdProducto());

        if (itemExistente.isPresent()){
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + request.getCantidad());
            itemCarritoRepository.save(item);
        } else {
            ItemCarrito nuevoItem = ItemCarritoMapper.toEntity(request);
            nuevoItem.setCarrito(carrito);
            itemCarritoRepository.save(nuevoItem);
        }

        return carritoMapper.toResponse(carrito);

    }

    @Transactional
    public CarritoResponse quitarItem(Integer usuarioId, Integer productoId){
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElseThrow(
            ()-> new EntityNotFoundException("Cliente", "ID", usuarioId));
        
        ItemCarrito item = itemCarritoRepository.findByProductoId(productoId).orElseThrow(
            ()-> new EntityNotFoundException("Item", "ID", productoId));

        itemCarritoRepository.delete(item);

        return carritoMapper.toResponse(carrito);

    }


    @Transactional
    public CarritoResponse vaciarCarrito(Integer usuarioId){
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElseThrow(
            ()-> new EntityNotFoundException("Cliente", "ID", usuarioId));

        itemCarritoRepository.deleteByCarritoId(usuarioId);
        return carritoMapper.toResponse(carrito);
    }



}
