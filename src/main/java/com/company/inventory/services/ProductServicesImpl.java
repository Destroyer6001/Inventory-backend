package com.company.inventory.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServicesImpl implements IProductService{
	
	private ICategoryDao categoryDao;
	private IProductDao productDao; 
	
	public ProductServicesImpl(ICategoryDao categoryDao, IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}

	@Override
	@Transactional 
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			Optional<Category> category = categoryDao.findById(categoryId);
			
			if(category.isPresent())
			{
				product.setCategory(category.get());
				Product productSave = productDao.save(product);
				
				if(productSave != null)
				{
					list.add(productSave);
					response.getProductResponse().setProduct(list);	
					response.setMetadata("Respuesta Ok","00", "Producto creado con exito");
				}
				else
				{
					response.setMetadata("Respuesta Nok", "-1", "El producto no pudo ser registrado en el sistema");
					return new ResponseEntity<ProductResponseRest>(response,HttpStatus.BAD_REQUEST);
					
				}
				
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Categoria no encontrada en el sistema");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
		}catch(Exception e){
			e.getStackTrace();
			response.setMetadata("Respuesta Nok", "-1", "Upps ha ocurrido un error durante la ejecucion");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchById(Long id) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			Optional<Product> producto = productDao.findById(id);
			
			if(producto.isPresent())
			{
				byte[] imagenDescomprimida = Util.decompressZLib(producto.get().getPicture());
				producto.get().setPicture(imagenDescomprimida);
				list.add(producto.get());
				response.getProductResponse().setProduct(list);
				response.setMetadata("Respuesta OK", "00", "Producto encontrado en el sistema");
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Producto no encontrado en el sistema");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
		}catch(Exception e)
		{
			e.getStackTrace();
			response.setMetadata("Respuesta Nok", "-1", "Upps hubo un error de comunicacion con la base de datos");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchByName(String name) {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> auxList = new ArrayList<>();
		
		try {
			
			auxList = productDao.findBynameContainingIgnoreCase(name);
			if(auxList.size() > 0)
			{
				auxList.stream().forEach((p) -> {
					byte[] imagendescomprimida = Util.decompressZLib(p.getPicture());
					p.setPicture(imagendescomprimida);
					list.add(p);
				});
				
				response.getProductResponse().setProduct(list);
				response.setMetadata("Respuesta Ok", "00", "La busqueda se ejecuto correctamente");
			}
			else
			{
				response.setMetadata("Respuesta nok", "-1", "No se encontraron productos con ese nombre");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
			
		} catch(Exception e)
		{
			e.getStackTrace();
			response.setMetadata("Respuesta nok", "-1", "Upps ha ocurrido un error inesperado");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> deleteById(Long id) {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			Optional<Product> product = productDao.findById(id);
			if(product.isPresent())
			{
				productDao.deleteById(id);
				list.add(product.get());
				response.getProductResponse().setProduct(list);
				response.setMetadata("Respuesta Ok", "00", "Producto eliminado con exito del sistema");
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "El producto que desea eliminar no se encuentra registrado en el sistema");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
		}catch(Exception e)
		{
			e.getStackTrace();
			response.setMetadata("Respuesta Nok", "-1", "Upss ha ocurrido un error inesperado");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly= true)
	public ResponseEntity<ProductResponseRest> search() {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> auxList = new ArrayList<>();
		
		try {
			
			auxList =  (List<Product>) productDao.findAll();
			
			if(auxList.size() > 0)
			{
				auxList.stream().forEach((p)->{
					byte[] descomprimirImagen = Util.decompressZLib(p.getPicture());
					p.setPicture(descomprimirImagen);
					list.add(p);
				});
				
				response.getProductResponse().setProduct(list);
				response.setMetadata("Respuesta Ok", "00", "Productos encontrados");
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Actualmente no hay productos registrados en el sistema");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.BAD_REQUEST);
			}
			
		}catch(Exception e)
		{
			response.setMetadata("Respuesta Nok", "-1", "Upps ha ocurrido un error inesperado");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> update(Product product, Long categoryId, Long id) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			Optional<Category> category = categoryDao.findById(categoryId);
			
			if(category.isPresent())
			{
				product.setCategory(category.get());
				Optional<Product> productoBusqueda = productDao.findById(id);
				productoBusqueda.get().setName(product.getName());
				productoBusqueda.get().setPrice(product.getPrice());
				productoBusqueda.get().setStock(product.getStock());
				productoBusqueda.get().setCategory(product.getCategory());
				productoBusqueda.get().setPicture(product.getPicture());
				
				Product productUpdate = productDao.save(productoBusqueda.get());
				
				if(productUpdate != null)
				{
					list.add(productUpdate);
					response.getProductResponse().setProduct(list);
					response.setMetadata("Respuesta Ok", "00", "Producto actualizado correctamente");
				}
				else
				{
					response.setMetadata("Respuesta Nok", "-1", "Lo sentimos pero ocurrio un error al actualizar el producto");
					return new ResponseEntity<ProductResponseRest>(response,HttpStatus.NOT_FOUND);
				}
				
				
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Lo sentimos pero la categoria seleccionada no se encuentra registrada en el sistema");
				return new ResponseEntity<ProductResponseRest>(response,HttpStatus.BAD_REQUEST);
			}
			
		}catch(Exception e)
		{
			response.setMetadata("Respuesta Nok", "-1", "Upps ha ocurrido un error inesperado");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

}
