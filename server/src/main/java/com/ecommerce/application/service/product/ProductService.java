package com.ecommerce.application.service.product;

import com.ecommerce.adapter.in.web.product.dto.ProductDTO;
import com.ecommerce.adapter.out.persistence.enums.MediaType;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductMediaEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductMediaRepository;
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

    public ProductService(ProductPort productPort,
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
                .weight(productDTO.getWeight())
                .height(productDTO.getHeight())
                .width(productDTO.getWidth())
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
    @Transactional
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        if (id == null || id.isBlank()) {
            throw new ProductNotFoundException("Product ID is required");
        }

        ProductEntity product = productPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getSlug() != null && !productDTO.getSlug().isBlank()) {
            product.setSlug(productDTO.getSlug());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
            product.setSku(productDTO.getSku());
        }
        if (productDTO.getPrice() != null && productDTO.getPrice().signum() >= 0) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getStockQuantity() != null && productDTO.getStockQuantity() >= 0) {
            product.setStockQuantity(productDTO.getStockQuantity());
        }
        if (productDTO.getStatus() != null && !productDTO.getStatus().isBlank()) {
            product.setStatus(productDTO.getStatus());
        }
        if (productDTO.getWeight() != null) {
            product.setWeight(productDTO.getWeight());
        }
        if (productDTO.getHeight() != null) {
            product.setHeight(productDTO.getHeight());
        }
        if (productDTO.getWidth() != null) {
            product.setWidth(productDTO.getWidth());
        }

        if (productDTO.getCategory() != null && !productDTO.getCategory().isEmpty()) {
            Set<CategoryEntity> categories = new HashSet<>();
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
            product.setCategories(categories);
        }

        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            List<ProductMediaEntity> existingMedia = productMediaRepository.findByProductIdOrderBySortOrderAsc(id);
            int sortOrder = existingMedia.size();
            List<ProductMediaEntity> newMediaList = new ArrayList<>();
            List<String> uploadedPublicIds = new ArrayList<>();

            try {
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
                            .isPrimary(sortOrder == 0 && existingMedia.isEmpty())
                            .build();

                    newMediaList.add(media);
                    sortOrder++;
                }

                if (!newMediaList.isEmpty()) {
                    productMediaRepository.saveAll(newMediaList);
                }
            } catch (Exception e) {
                for (String publicId : uploadedPublicIds) {
                    try {
                        fileStoragePort.delete(publicId);
                    } catch (Exception ignored) {
                    }
                }
                throw e;
            }
        }

        ProductEntity updated = productPort.saveProduct(product);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        if (id == null || id.isBlank()) {
            throw new ProductNotFoundException("Product ID is required");
        }

        ProductEntity product = productPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        List<ProductMediaEntity> mediaList = productMediaRepository.findByProductIdOrderBySortOrderAsc(id);
        for (ProductMediaEntity media : mediaList) {
            if (media.getPublicId() != null && !media.getPublicId().isBlank()) {
                try {
                    fileStoragePort.delete(media.getPublicId());
                } catch (Exception ignored) {
                }
            }
        }

        if (!mediaList.isEmpty()) {
            productMediaRepository.deleteAll(mediaList);
        }

        productPort.deleteById(id);
    }

    @Override
    public ProductDTO getProduct(String id) {
        if (id == null || id.isBlank()) {
            throw new ProductNotFoundException("Product ID is required");
        }

        ProductEntity product = productPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        return mapToDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productPort.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(String categoryId) {
        return productPort.findByCategoryId(categoryId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getProductsByCategoryName(String categoryName) {
        return productPort.findByCategoryName(categoryName).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ProductDTO mapToDTO(ProductEntity product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = ProductDTO.fromEntity(product);

        List<ProductMediaEntity> mediaList = productMediaRepository.findByProductIdOrderBySortOrderAsc(product.getId());
        if (mediaList != null && !mediaList.isEmpty()) {
            dto.setImagesList(
                    mediaList.stream()
                            .sorted(Comparator.comparing(ProductMediaEntity::getSortOrder))
                            .map(ProductMediaEntity::getMediaUrl)
                            .toList()
            );
        }

        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            dto.setCategory(
                    product.getCategories().stream()
                            .map(CategoryEntity::getName)
                            .toList()
            );
        }

        return dto;
    }
}

