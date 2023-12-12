package com.tiennam.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private int totalProduct;
    private double totalPrice;
    private int totalItems;
    private Long totalPrices;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private List<CartProduct> cartProduct;

    public Long getTotalPrices() {
        long cartTotal = 0;
        for(CartProduct product : this.cartProduct){
            cartTotal += product.getTotalPrice();
        }
        return cartTotal;
    }

    public CartProduct findCartProductByProductAndSize(String id, int size){
        for (CartProduct cartProduct : this.cartProduct) {
            if(cartProduct.getProduct().getId().equals(id) && cartProduct.getSize()==size){
                return cartProduct;
            }
        }
        return null;
    }

    public Cart(User user) {
        this.user = user;
    }

    public boolean isEmpty() {
        return cartProduct.isEmpty();
    }
    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    @Column(name = "price")
//    private String price;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL)
//    private List<CartProduct> cartProducts = new ArrayList<>();
}
