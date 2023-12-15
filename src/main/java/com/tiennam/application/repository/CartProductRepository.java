package com.tiennam.application.repository;


import com.tiennam.application.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct,Long> {

    @Query(value = "select * from product_cart where cart_id = ?1",nativeQuery = true)
    public List<CartProduct> getAllById(Long id);

}
