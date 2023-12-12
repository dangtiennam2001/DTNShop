package com.tiennam.application.service.impl;

import com.tiennam.application.entity.Cart;
import com.tiennam.application.entity.CartProduct;
import com.tiennam.application.entity.Product;
import com.tiennam.application.entity.User;
import com.tiennam.application.repository.CartProductRepository;
import com.tiennam.application.repository.CartRepository;
import com.tiennam.application.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart findByUserId(long id) {
        return cartRepository.findCartByUserId(id);
    }

    @Override
    public CartProduct addItemToCart(Product product, int quantity, int size,User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart(user);
        }
        List<CartProduct> cartProducts = cart.getCartProduct();
        //CartProduct cartProduct = findCartProduct(cartProducts, product.getId());
        CartProduct cartProduct = cart.findCartProductByProductAndSize(product.getId(),size);
        if (cartProduct != null) {
            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
            cartProduct.setSize(size);
            cartProduct.setTotalPrice(cartProduct.getTotalPrice() + ( quantity * product.getSalePrice()));
            cartProduct = cartProductRepository.save(cartProduct);
        }
        else {
            cartProduct = new CartProduct();
            cartProduct.setProduct(product);
            cartProduct.setTotalPrice(quantity * product.getSalePrice());
            cartProduct.setQuantity(quantity);
            cartProduct.setSize(size);
            cartProduct.setCart(cart);
            cartProducts.add(cartProduct);
            cartProduct = cartProductRepository.save(cartProduct);
        }

        return cartProduct;
    }

    @Override
    public Cart updateItemToCart(Product product, int quantity, User user) {
        Cart cart = user.getCart();

        List<CartProduct> cartItems = cart.getCartProduct();

        CartProduct item = findCartProduct(cartItems, product.getId());

        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getPrice());

        cartProductRepository.save(item);

        int totalItems = totalItems(cartItems);
//        double totalPrice = totalPrice(cartItems);

        cart.setTotalItems(totalItems);
//        cart.setTotalPrices(totalPrice);

        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getAllCart() {
        return cartRepository.findAll();
    }

    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    //    @Override
//    public Cart deleteItemFromCart(Product product, User user) {
//        Cart cart = user.getCart();
//
//        Set<CartProduct> cartProducts = cart.getCartProduct();
//
//        CartProduct item = findCartProduct(cartProducts, product.getId());
//
//        cartProducts.remove(item);
//
//        cartRepository.delete(item);
//
//        double totalPrice = totalPrice(cartProducts);
//        int totalItems = totalItems(cartProducts);
//
//        cart.setCartProduct(cartProducts);
//        cart.setTotalItems(totalItems);
//        cart.setTotalPrices(totalPrice);
//
//        return cartRepository.save(cart);
//    }



    private CartProduct findCartProduct(List<CartProduct> cartProducts,String productId) {
        for (CartProduct product : cartProducts) {
            if (product.getProduct().getId() == productId) {
                return product;
            }
        }
        return null;
    }

    private int totalItems(List<CartProduct> cartProducts){
        int totalItems = 0;
        for(CartProduct product : cartProducts){
            totalItems += product.getId();
        }
        return totalItems;
    }

}
