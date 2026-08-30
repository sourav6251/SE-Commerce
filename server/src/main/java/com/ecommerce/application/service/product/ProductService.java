package com.ecommerce.application.service.product;

import com.ecommerce.adapter.in.web.product.dto.ProductDTO;
import com.ecommerce.adapter.out.persistence.enums.MediaType;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductMediaEntity;
//import com.ecommerce.adapter.out.persistence.jpa.repository.CategoryRepository;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductMediaRepository;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductRepository;
import com.ecommerce.adapter.out.storage.cloudinary.dto.FileUploadResponseDTO;
import com.ecommerce.application.port.in.product.ProductUseCase;
import com.ecommerce.application.port.out.persistence.CategoryPort;
import com.ecommerce.application.port.out.persistence.ProductPort;
import com.ecommerce.application.port.out.storage.FileStoragePort;
import com.ecommerce.domain.exception.CategotyExcaption;
import com.ecommerce.domain.exception.MediaTypeException;
import com.ecommerce.domain.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService implements ProductUseCase {

    private final ProductPort productPort;
    private final CategoryPort categoryPort;
    private final ProductMediaRepository productMediaRepository;
    private final FileStoragePort fileStoragePort;

    public ProductService( ProductPort productPort,
                          ProductMediaRepository productMediaRepository,
                          CategoryPort categoryPort,
                          @Qualifier("imageKitAdapter") FileStoragePort fileStoragePort) {
        this.productPort = productPort;
        this.productMediaRepository = productMediaRepository;
        this.fileStoragePort = fileStoragePort;
        this.categoryPort = categoryPort;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new ProductNotFoundException("Product data cannot be null");
        }

        if (productDTO.getName() == null || productDTO.getName().isBlank()) {
            throw new ProductNotFoundException("Product name is required");
        }

        if (productDTO.getPrice() == null || productDTO.getPrice().signum() < 0) {
            throw new ProductNotFoundException("Product price is invalid");
        }

        if (productDTO.getStockQuantity() == null || productDTO.getStockQuantity() < 0) {
            throw new ProductNotFoundException("Product stock quantity is invalid");
        }

        Set<CategoryEntity> categories = new HashSet<>();
        if (productDTO.getCategory() != null && !productDTO.getCategory().isEmpty()) {
            for (String categoryId : productDTO.getCategory()) {
                if (categoryId == null || categoryId.isBlank()) {
                    continue;
                }
                CategoryEntity cat = categoryPort.findByID(categoryId.trim());
                if (cat == null) {
                    throw new CategotyExcaption("Category not found with ID: " + categoryId);
                }
                categories.add(cat);
            }
        }

        ProductEntity product = ProductEntity.builder()
                .categories(categories)
                .name(productDTO.getName())
                .slug(productDTO.getSlug())
                .description(productDTO.getDescription())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .status(productDTO.getStatus() != null ? productDTO.getStatus() : "ACTIVE")
                .build();

        product = productPort.saveProduct(product);

        List<ProductMediaEntity> savedMediaList = new ArrayList<>();
        List<String> uploadedPublicIds = new ArrayList<>();

        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            try {
                int sortOrder = 0;
                for (MultipartFile file : productDTO.getImages()) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }

                    FileUploadResponseDTO uploadResponse = fileStoragePort.upload(file);
                    if (uploadResponse == null || uploadResponse.url() == null || uploadResponse.url().isBlank()) {
                        throw new RuntimeException("Failed to upload product media: " + file.getOriginalFilename());
                    }

                    if (uploadResponse.publicId() != null) {
                        uploadedPublicIds.add(uploadResponse.publicId());
                    }

                    MediaType mediaType;
                    String contentType = file.getContentType();
                    if (contentType != null && contentType.toLowerCase().startsWith("video/")) {
                        mediaType = MediaType.VIDEO;
                    } else if (contentType != null && contentType.toLowerCase().startsWith("image/")) {
                        mediaType = MediaType.IMAGE;
                    } else {
                        throw new MediaTypeException("Unsupported product media type: " + contentType);
                    }

                    ProductMediaEntity media = ProductMediaEntity.builder()
                            .product(product)
                            .mediaUrl(uploadResponse.url())
                            .publicId(uploadResponse.publicId())
                            .mediaType(mediaType)
                            .sortOrder(sortOrder)
                            .isPrimary(sortOrder == 0)
                            .build();

                    savedMediaList.add(media);
                    sortOrder++;
                }

                if (!savedMediaList.isEmpty()) {
                    savedMediaList = productMediaRepository.saveAll(savedMediaList);
                }
            } catch (Exception e) {
                // Cleanup uploaded media files in storage on failure
                for (String publicId : uploadedPublicIds) {
                    try {
                        fileStoragePort.delete(publicId);
                    } catch (Exception ignored) {
                    }
                }
                throw e;
            }
        }

        ProductDTO response = ProductDTO.fromEntity(product);

        if (!savedMediaList.isEmpty()) {
            response.setImagesList(
                    savedMediaList.stream()
                            .sorted(Comparator.comparing(ProductMediaEntity::getSortOrder))
                            .map(ProductMediaEntity::getMediaUrl)
                            .toList()
            );
        }

        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            response.setCategory(
                    product.getCategories().stream()
                            .map(CategoryEntity::getName)
                            .toList()
            );
        }

        return response;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }

    @Override
    public ProductDTO getProduct(Long id) {
        return null;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return List.of();
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        return List.of();
    }

    @Override
    public List<ProductDTO> getProductsByCategoryName(String categoryName) {
        return List.of();
    }
}
