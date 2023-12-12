package com.tiennam.application.service.impl;

import com.tiennam.application.entity.CartProduct;
import com.tiennam.application.exception.InternalServerException;
import com.tiennam.application.repository.CartProductRepository;
import com.tiennam.application.repository.CartRepository;
import com.tiennam.application.repository.ProductRepository;
import com.tiennam.application.service.CartProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartProductServiceImpl implements CartProductService {

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartProduct create(int quantity, String productId, long cartId, int size) {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setQuantity(quantity);
        cartProduct.setSize(size);
        cartProduct.setProduct(productRepository.findById(productId).get());
        cartProduct.setCart(cartRepository.findById(cartId).get());
        cartProduct.setTotalPrice(cartProduct.getQuantity()*(cartProduct.getProduct().getSalePrice()));
        return cartProductRepository.save(cartProduct);
    }

    @Override
    public void delete(Long id) {
        Optional<CartProduct> cartProduct = cartProductRepository.findById(id);
        try {
            cartProductRepository.delete(cartProduct.get());
            System.out.println("Log: >>>>" + cartProduct.get());
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa giỏ hàng!");
        }
    }

    @Override
    public void deleteAll() {
        try {
            cartProductRepository.deleteAll();
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa giỏ hàng!");
        }
    }

    @Override
    public List<CartProduct> getAllById(Long id) {
        return cartProductRepository.getAllById(id);
    }

//    @Override
//    public Lis0t<CartProduct> getAllById(Long id) {
//        return cartProductRepository.getAllById(id);
//    }


    @Override
    public CartProduct getCartProductById(Long id) {
        Optional<CartProduct> cartProduct = cartProductRepository.findById(id);
        return cartProduct.get();
    }

    @Override
    public void updateCartProduct(CartProduct cartProduct, Integer quantity) {
        if(quantity==null || quantity <= 0)
            this.delete(cartProduct.getId());
        else{
            cartProduct.setQuantity(quantity);
            cartProductRepository.save(cartProduct);
        }
    }
}
