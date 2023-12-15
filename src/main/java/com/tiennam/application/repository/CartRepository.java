package com.tiennam.application.repository;

import com.tiennam.application.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query(value = "select * from cart where user_id = ?1",nativeQuery = true)
    Cart findCartByUserId(long id);

}
