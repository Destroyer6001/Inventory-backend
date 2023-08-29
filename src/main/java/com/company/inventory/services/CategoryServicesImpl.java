package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.response.CategoryResponseRest;

@Service
public class CategoryServicesImpl implements ICategoryService{
	
	@Autowired
	private ICategoryDao categoryDao; 

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<CategoryResponseRest> search() {
		
		CategoryResponseRest response = new CategoryResponseRest();
		try
		{
			List<Category> category = (List<Category>) categoryDao.findAll();
			response.getCategoryResponse().setCategory(category);
			response.setMetadata("Respuesta ok", "00", "Respuesta exitosa");
		}
		catch(Exception e)
		{
			response.setMetadata("Respues Nok", "-1", "Error al consultar");
			e.getStackTrace();
			
			return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly=true)
	public ResponseEntity<CategoryResponseRest> searchById(Long id) 
	{
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		
		try
		{
			Optional<Category> category = categoryDao.findById(id);
			
			if(category.isPresent())
			{
				list.add(category.get());
				response.getCategoryResponse().setCategory(list);
				response.setMetadata("Respuesta ok", "00", "Respuesta exitosa");
			}
			else
			{
				response.setMetadata("Respues Nok", "-1", "Categoria No Encontrada");
				return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
			}
		}
		catch(Exception e)
		{
			response.setMetadata("Respues Nok", "-1", "Error al consultar por id");
			e.getStackTrace();
			
			return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> save(Category category) {
		
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		
		try
		{
			Category categorySave = categoryDao.save(category);
			
			if(categorySave != null)
			{
				list.add(categorySave);
				response.getCategoryResponse().setCategory(list);
				response.setMetadata("Respuesta ok", "00", "categoria guardada correctamente");
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Debes haber ingresado mal un campo");
				return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.BAD_REQUEST);
			}
			
		}
		catch(Exception e)
		{
			response.setMetadata("Respues Nok", "-1", "Error guardar una categoria");
			e.getStackTrace();
			
			return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.OK);
		
	}

	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> update(Long id, Category category) {
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		try
		{
			Optional<Category> categorySearch = categoryDao.findById(id);
			
			if(categorySearch.isPresent())
			{
				categorySearch.get().setName(category.getName());
				categorySearch.get().setDiscription(category.getDiscription());
				Category categoryUpdate = categoryDao.save(categorySearch.get());
				
				if(categoryUpdate != null)
				{
					list.add(categoryUpdate);
					response.getCategoryResponse().setCategory(list);
					response.setMetadata("Respuesta Ok", "00", "Categoria actualizada correctamente");
				}
				else
				{
					response.setMetadata("Respuesta Nok", "-1", "Error al actualizar la categoria" );
					return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
				}
			}
			else
			{
				response.setMetadata("Respuesto Nok", "-1", "Categoria a actualizar no encontrada");
				return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
		}
		catch(Exception e)
		{
			response.setMetadata("Respues Nok", "-1", "Error al actualizar una categoria");
			e.getStackTrace();
			
			return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<CategoryResponseRest> delete(Long id) {
		CategoryResponseRest response = new CategoryResponseRest();
		List<Category> list = new ArrayList<>();
		
		try
		{
			Optional<Category> categorySearch = categoryDao.findById(id);
			
			if(categorySearch.isPresent())
			{
				categoryDao.deleteById(id);
				list.add(categorySearch.get());
				response.getCategoryResponse().setCategory(list);
				response.setMetadata("Respuesta Ok", "00", "Categoria eliminada");
			}
			else
			{
				response.setMetadata("Respuesta Nok", "-1", "Categoria a eliminar no encontrada");
				return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
			}
			
		}
		catch(Exception e)
		{
			response.setMetadata("Respues Nok", "-1", "Error al eliminar una categoria");
			e.getStackTrace();
			
			return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CategoryResponseRest>(response,HttpStatus.OK);
		
		
	}

}
