package com.company.inventory.services;

import java.util.List;

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

}
