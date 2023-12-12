package com.tiennam.application.service;

import com.tiennam.application.entity.Cart;
import com.tiennam.application.entity.CartProduct;
import com.tiennam.application.entity.Product;
import com.tiennam.application.entity.User;

import java.util.List;


public interface CartService {
    Cart findByUserId(long id);

    CartProduct addItemToCart(Product product, int quantity, int size,User user);

    Cart updateItemToCart(Product product,int quantity,User user);

    List<Cart> getAllCart();

    void save(Cart cart);
//    Cart deleteItemFromCart(Product product, User user);

//    List<CartDTO> getListCartOfPersonByStatus(int status, long userId);
//    ShoppingCart addItemToCart(Product product, int quantity, Customer customer);
//
//    ShoppingCart updateItemInCart(Product product, int quantity, Customer customer);
//
//    ShoppingCart deleteItemFromCart(Product product, Customer customer);
}
