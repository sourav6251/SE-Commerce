package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, String> {

    List<AddressEntity> findByUserId(String userId);

    List<AddressEntity> findByUserEmail(String email);

    @Query("SELECT a FROM AddressEntity a WHERE a.id = :id AND (a.user.id = :userIdentifier OR a.user.email = :userIdentifier)")
    Optional<AddressEntity> findByIdAndUserIdentifier(@Param("id") String id, @Param("userIdentifier") String userIdentifier);

    @Query("SELECT a FROM AddressEntity a WHERE a.user.id = :userIdentifier OR a.user.email = :userIdentifier")
    List<AddressEntity> findByUserIdentifier(@Param("userIdentifier") String userIdentifier);

    @Query("SELECT a FROM AddressEntity a WHERE (a.user.id = :userIdentifier OR a.user.email = :userIdentifier) AND a.isDefault = true")
    List<AddressEntity> findDefaultsByUserIdentifier(@Param("userIdentifier") String userIdentifier);
}
