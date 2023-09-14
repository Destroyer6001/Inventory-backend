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
			response.setMetadata("Respuesta Nok", "-1", "Upps hubo un error de comunicacion con la base de datos");
			return new ResponseEntity<ProductResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response,HttpStatus.OK);
	}

}
