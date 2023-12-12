package com.tiennam.application.service;

import com.tiennam.application.entity.CartProduct;

import java.util.List;

public interface CartProductService {
    public CartProduct create(int quantity, String productId, long cartId, int size);
    public void delete(Long id);
    public void deleteAll();
    public List<CartProduct> getAllById(Long id);
    public CartProduct getCartProductById(Long id);
    public void updateCartProduct(CartProduct cartProduct, Integer quantity);
}
