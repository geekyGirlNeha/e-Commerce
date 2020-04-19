package com.example.SpringSecurity.dao;

import com.example.SpringSecurity.dto.*;
import com.example.SpringSecurity.entity.products.Category;
import com.example.SpringSecurity.entity.products.CategoryMetadataField;
import com.example.SpringSecurity.entity.products.CategoryMetadataFieldValue;
import com.example.SpringSecurity.entity.products.CategoryMetadataFieldValueId;
import com.example.SpringSecurity.exceptions.CategoryException;
import com.example.SpringSecurity.repository.CategoryMetadataFieldRepository;
import com.example.SpringSecurity.repository.CategoryMetadataFieldValueRepository;
import com.example.SpringSecurity.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CategoryDao {

    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;

    public String addMetadataField(String fieldName){
        if (fieldName ==null || fieldName =="") {
            throw new NullPointerException("Please Enter fieldName");
        }
        else {
            CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.findByName(fieldName);
            if(categoryMetadataField != null){
                return "Field already exists at field_id: " + categoryMetadataField.getId();
            }
            else {
                CategoryMetadataField categoryMetadataFieldObject = new CategoryMetadataField();
                categoryMetadataFieldObject.setName(fieldName);
                categoryMetadataFieldRepository.save(categoryMetadataFieldObject);

                return "Field saved at field_id: " + categoryMetadataFieldRepository.findByName(fieldName).getId();
            }
        }
    }


    public List<CategoryMetadataField> getAllMetadataFields(Integer size, Integer offset, String field, String order){
        Pageable pageable;

        if (order.equalsIgnoreCase("ASC"))
            pageable = PageRequest.of(offset, size, Sort.Direction.ASC, field);
        else
            pageable = PageRequest.of(offset, size, Sort.Direction.DESC, field);

        return categoryMetadataFieldRepository.findAll(pageable);
    }



    public String addCategory(String categoryName, Long parentId){
        if(checkNull(categoryName) && parentId != null){
            if(categoryRepository.findByName(categoryName)!= null){
                throw new CategoryException("Category already Exists");
            }
            else if(!categoryRepository.findById(parentId).isPresent()){
                throw new CategoryException("Parent Id does not exists");
            }
            else {
                Category category = new Category();
                category.setName(categoryName);
                category.setParentId(parentId);
                categoryRepository.save(category);
                return "Category saved at category_id: " + categoryRepository.findByName(categoryName).getId();
            }
        }
        else if (checkNull(categoryName) && parentId == null){
            if (categoryRepository.findByName(categoryName) != null){
                throw new CategoryException("Category already exists");
            }
            else {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
                return "Category saved at category_id: " + categoryRepository.findByName(categoryName).getId();
            }
        }
        else {
            throw new NullPointerException("Enter Category name");
        }
    }

    private Boolean checkNull(String value){
        if(value != null && !value.equals(""))
            return true;
        else
            return false;
    }



    public SingleCategoryDto getSingleCategory(Long categoryId){
        Category category = categoryRepository.findByid(categoryId);

        List<String> emptyList = new ArrayList<>();
        List<String> categoriesListWithParent = getCategoriesParent(categoryId, emptyList);
        List<Category> childCategory = categoryRepository.findByParentId(category.getId());

        SingleCategoryDto singleCategoryDto = new SingleCategoryDto();
        singleCategoryDto.setId(categoryId);
        singleCategoryDto.setCategoriesList(categoriesListWithParent);
        if (!childCategory.isEmpty()){
            singleCategoryDto.setChildCategory(childCategory.get(0).getName());
        }
        else {
            singleCategoryDto.setChildCategory("Child category doesn't exists");
        }

        return singleCategoryDto;
    }

    private List<String> getCategoriesParent(Long id, List<String> categoriesList){
        Category category = categoryRepository.findByid(id);
        categoriesList.add(category.getName());
        if (category.getParentId() != null){
            return getCategoriesParent(category.getParentId(), categoriesList);
        }
        else {
            return categoriesList;
        }
    }


    public List<SingleCategoryDto> getAllCategory(Integer size, Integer offset, String field, String order){
        Pageable pageable;

        if (order.equalsIgnoreCase("ASC"))
            pageable = PageRequest.of(offset, size, Sort.Direction.ASC, field);
        else
            pageable = PageRequest.of(offset, size, Sort.Direction.DESC, field);

        List<SingleCategoryDto> singleCategoryDtoList = new ArrayList<>();
        List<Category> allCategory = categoryRepository.findAll(pageable);

        allCategory.forEach(category -> {
            singleCategoryDtoList.add(getSingleCategory(category.getId()));
        });
        return singleCategoryDtoList;
    }



    public String updateCategory(Long categoryId, String categoryName){
        Category category = categoryRepository.findByid(categoryId);

        if (category != null){
            if (categoryRepository.findByName(categoryName) == null){
                category.setName(categoryName);
                categoryRepository.save(category);
                return "Category Updated Successfully";
            }
            else {
                throw new CategoryException("Category with this name already exists");
            }
        }
        else {
            throw new CategoryException("Category doesn't exist");
        }
    }



    public String addMetadataFieldValue(CategoryMetadataFieldValueDto categoryMetadataFieldValueDto){
        Optional<Category> category = categoryRepository.findById(categoryMetadataFieldValueDto.getCategoryId());
        Optional<CategoryMetadataField> categoryMetadataField = categoryMetadataFieldRepository.findById(categoryMetadataFieldValueDto.getFieldId());

        if (category.isPresent()){
            if (categoryMetadataField.isPresent()){
                CategoryMetadataFieldValue categoryMetadataFieldValue = new CategoryMetadataFieldValue();
                CategoryMetadataFieldValueId categoryMetadataFieldValueId = new CategoryMetadataFieldValueId();

                categoryMetadataFieldValueId.setCategory_id(category.get().getId());
                categoryMetadataFieldValueId.setCategory_metadata_field_id(categoryMetadataField.get().getId());

                categoryMetadataFieldValue.setCategoryMetadataFieldValueId(categoryMetadataFieldValueId);
                categoryMetadataFieldValue.setCategory(category.get());
                categoryMetadataFieldValue.setCategoryMetadataField(categoryMetadataField.get());

                String values = SetStringConverter.convertToString(categoryMetadataFieldValueDto.getFieldValues());
                categoryMetadataFieldValue.setValues(values);

                categoryMetadataFieldValueRepository.save(categoryMetadataFieldValue);

                return "Category Metadata Fields Values saved successfully";
            }
            else
                throw new CategoryException("Category Metadata Field doesn't exists");
        }
        else
            throw new CategoryException("Category doesn't exists");
    }



    public String updateMetadataField(CategoryMetadataFieldValueDto categoryMetadataFieldValueDto){
        CategoryMetadataFieldValue categoryMetadataFieldValue = categoryMetadataFieldValueRepository
                                                                    .findByCategoryAndMetadataField(categoryMetadataFieldValueDto.getCategoryId(),
                                                                                                    categoryMetadataFieldValueDto.getFieldId());
        if (categoryMetadataFieldValue!= null){
            String updateValues = validateMetadataFieldValues(categoryMetadataFieldValueDto.getFieldValues(),
                                                                categoryMetadataFieldValueDto.getCategoryId(),
                                                                categoryMetadataFieldValueDto.getFieldId());
            categoryMetadataFieldValue.setValues(updateValues);
            categoryMetadataFieldValueRepository.save(categoryMetadataFieldValue);
            return "Category Metadata Field Updated Successfully";
        }
        else {
            throw new CategoryException("Enter valid category and metadata field");
        }
    }


    private String validateMetadataFieldValues(Set<String> fieldValues, Long categoryId, Long fieldId){
        String value = categoryMetadataFieldValueRepository.findByCategoryAndMetadataField(categoryId, fieldId).getValues();
        if (!fieldValues.isEmpty()){
            Set<String> valueSet = SetStringConverter.convertToSet(value);
            Set<String> intersectionSet = findIntersection(fieldValues, valueSet);
            if (intersectionSet.isEmpty()){
                String updatedValue = value + "," + SetStringConverter.convertToString(fieldValues);
                return updatedValue;
            }
            else {
                throw new CategoryException("Field Already Exists: " + SetStringConverter.convertToString(intersectionSet));
            }
        }
        else {
            throw new CategoryException("Enter at least one field value to update");
        }

    }

    private Set<String> findIntersection(Set<String> stringSet1, Set<String> stringSet2){
        Set<String> intersection = new HashSet<>(stringSet1);
        intersection.retainAll(stringSet2);
        return intersection;
    }


    public List<CategoryForSellerDto> getCategoryForSeller(){

        List<CategoryForSellerDto> categorySellerDtos=new ArrayList<>();
        List<Category> leafCategories=categoryRepository.getLeafCategory();
        leafCategories.forEach(category -> {
            CategoryForSellerDto categorySellerDto=new CategoryForSellerDto();
            categorySellerDto.setCategoryId(category.getId());
            categorySellerDto.setCategoryName(category.getName());
            categorySellerDto.setCategoryMetadataDtoList(getCategoryMetadata(category.getId()));
            Long parentId=category.getParentId();
            if (parentId!=null){
                List<String> parentCategories=new ArrayList<>();
                categorySellerDto.setParentCategories(getCategoriesParent(parentId,parentCategories));
            }
            categorySellerDtos.add(categorySellerDto);
        });
        return categorySellerDtos;
    }

    private List<CategoryMetadataDto> getCategoryMetadata(Long id) {
        List<CategoryMetadataDto> categoryMetadataDtoList=new ArrayList<>();
        List<Object[]> metadataList=categoryRepository.getMetadataByCategoryId(id);
        for (Object[] metadata:metadataList) {
            CategoryMetadataDto categoryMetadataDto=new CategoryMetadataDto((String) metadata[0],(String) metadata[1]);
            categoryMetadataDtoList.add(categoryMetadataDto);
        }
        return categoryMetadataDtoList;
    }


    public List<CategoryDto> getAllCategoriesCustomer(Long id){
        List<CategoryDto> allCategories=new ArrayList<>();
        if(id!=null){
            if (!categoryRepository.findById(id).isPresent())
                throw new CategoryException("Category with this id does not exist!");
            List<Category> childCategory=categoryRepository.findByParentId(id);
            CategoryDto categoryDto=new CategoryDto(childCategory.get(0).getId(),childCategory.get(0).getName());
            allCategories.add(categoryDto);
            return allCategories;

        }
        else{
            List<Category> rootParents=categoryRepository.getRootParent();
            rootParents.forEach(category -> {
                CategoryDto categoryDto=new CategoryDto(category.getId(),category.getName());
                allCategories.add(categoryDto);
            });
        }
        return allCategories;
    }




}
