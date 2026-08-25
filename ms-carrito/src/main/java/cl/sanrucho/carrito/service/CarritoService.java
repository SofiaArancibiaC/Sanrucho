package cl.sanrucho.carrito.service;

import org.springframework.stereotype.Service;

import cl.sanrucho.carrito.dto.CarritoRequest;
import cl.sanrucho.carrito.dto.CarritoResponse;
import cl.sanrucho.carrito.mapper.CarritoMapper;
import cl.sanrucho.carrito.mapper.ItemCarritoMapper;
import cl.sanrucho.carrito.repository.CarritoRepository;
import cl.sanrucho.carrito.repository.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CarritoMapper carritoMapper;
    private final ItemCarritoMapper ItemCarritoMapper;
    
    public CarritoResponse crearCarrito(CarritoRequest request){
        

    }

}
