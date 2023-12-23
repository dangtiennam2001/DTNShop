package com.tiennam.application.controller.shop;

import com.tiennam.application.entity.*;
import com.tiennam.application.entity.*;
import com.tiennam.application.service.*;
import com.tiennam.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartProductService cartProductService;

    @Autowired
    private PromotionService promotionService;


    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session){
        if(principal==null) return "redirect:";

        String username = principal.getName();
        User user = userService.findByEmail(username);
        Cart cart = user.getCart();

        List<CartProduct> cartProducts = cartProductService.getAllById(cart.getId());

        if(cartProducts == null){
            CartProduct cartProduct = new CartProduct();
            cartProducts.add(cartProduct);
            model.addAttribute("check", "No item in your cart");
        }
        session.setAttribute("totalItems", cart.getTotalItems());
//        model.addAttribute("subTotal", cart.getTotalPrices());
        model.addAttribute("shoppingCart", cart);
//        model.addAttribute("cartId",cart.getId());
//        model.addAttribute("productCart",cartProducts);
        model.addAttribute("cartProductList", cart.getCartProduct());

        List<Product> products = productService.getAllProduct();
        Promotion promotion = promotionService.checkPublicPromotion();
        if(promotion!=null){
            if(promotion.getDiscountType()==1){
                for(Product product : products){
                    product.setSalePrice(product.getSalePrice() * (100 - promotion.getDiscountValue()) / 100);
                }
            }
            if(promotion.getDiscountType()==2){
                for(Product product : products){
                    product.setSalePrice(product.getSalePrice() - promotion.getDiscountValue());
                }
            }
        }
        model.addAttribute("promotion", promotion);
        return "shop/cart";
    }


    @GetMapping("/add-to-cart")
    public String addItemToCart(Model model, @RequestParam String id,@RequestParam int size,Principal principal) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
//        Cart cart = cartService.findByUserId(user.getId());

//        cartProductService.create(1,id,cart.getId(),size);
        cartService.addItemToCart(productService.getProductById(id),1, size, user);
        return "redirect:/cart";
    }

    @RequestMapping(value = "/update-cart", method = RequestMethod.POST)
    public String updateCart(@RequestParam("quantity") int quantity,
                             @RequestParam("id") long cartProductId,
                             Model model,
                             Principal principal
    ){
        CartProduct cartProduct = cartProductService.getCartProductById(cartProductId);
        cartProductService.updateCartProduct(cartProduct,quantity);

        String username = principal.getName();
        User user = userService.findByEmail(username);
//        Product product = productService.getProductById(productId);
//        Cart cart = cartService.updateItemToCart(product, quantity, user);
        Cart cart = user.getCart();
        model.addAttribute("shoppingCart", cart);
        return "redirect:/cart";

    }

    @GetMapping("/delete-cartItem/{id}")
    public String delete(@PathVariable long id) {
        cartProductService.delete(id);
        return "redirect:/cart";
    }

    @GetMapping("/delete-cartItem")
    public String deleteAll() {
        cartProductService.deleteAll();
        return "redirect:/cart";
    }
//    @DeleteMapping("/delete-cartItem/{id}")
//    public ResponseEntity<Object> deleteCartProduct(@PathVariable long id) {
//        cartProductService.delete(id);
//
//        return ResponseEntity.ok("Xóa thành công!");
//    }


//    @RequestMapping(value = "/update-cart", method = RequestMethod.GET, params = "action=delete")
//    public String deleteItemFromCart(@RequestParam long id,
//                                     Model model
//                                     ){
//            cartProductService.delete(id);
//            return "redirect:/cart";
//
//
//    }

}
