package com.tiennam.application.controller.shop;

import com.tiennam.application.model.dto.*;
import com.tiennam.application.model.request.CreateOrderRequest;
import com.tiennam.application.entity.*;
import com.tiennam.application.exception.BadRequestException;
import com.tiennam.application.exception.NotFoundException;
import com.tiennam.application.model.request.FilterProductRequest;
import com.tiennam.application.security.CustomUserDetails;
import com.tiennam.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tiennam.application.config.Contant.*;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private PostService postService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CartProductService cartProductService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String homePage(Model model){

        //Lấy 5 sản phẩm mới nhất
        List<ProductInfoDTO> newProducts = productService.getListNewProducts();
        model.addAttribute("newProducts", newProducts);

        //Lấy 5 sản phẩm bán chạy nhất
        List<ProductInfoDTO> bestSellerProducts = productService.getListBestSellProducts();
        model.addAttribute("bestSellerProducts", bestSellerProducts);

        //Lấy 5 sản phẩm có lượt xem nhiều
        List<ProductInfoDTO> viewProducts = productService.getListViewProducts();
        model.addAttribute("viewProducts", viewProducts);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands",brands);

        //Lấy 5 bài viết mới nhất
        List<Post> posts = postService.getLatesPost();
        model.addAttribute("posts", posts);

        return "shop/index";
    }

    @GetMapping("/{slug}/{id}")
    public String getProductDetail(Model model, @PathVariable String id){

        //Lấy thông tin sản phẩm
        DetailProductInfoDTO product;
        try {
            product = productService.getDetailProductById(id);
        } catch (NotFoundException ex) {
            return "error/404";
        } catch (Exception ex) {
            return "error/500";
        }
        model.addAttribute("product", product);

        //Lấy sản phẩm liên quan
        List<ProductInfoDTO> relatedProducts = productService.getRelatedProducts(id);
        model.addAttribute("relatedProducts", relatedProducts);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands",brands);

        // Lấy size có sẵn
        List<Integer> availableSizes = productService.getListAvailableSize(id);
        model.addAttribute("availableSizes", availableSizes);
        if (!availableSizes.isEmpty()) {
            model.addAttribute("canBuy", true);
        } else {
            model.addAttribute("canBuy", false);
        }

        //Lấy danh sách size giầy
        model.addAttribute("sizeVn", SIZE_VN);
        model.addAttribute("sizeUs", SIZE_US);
        model.addAttribute("sizeCm", SIZE_CM);

        return "shop/detail";
    }

    @GetMapping("/dat-hang")
    public String getCartPage(Model model, @RequestParam(required = false) String id,@RequestParam(required = false) Integer size,@RequestParam(required = false) Long cartid){

        try {
            List<DetailProductInfoDTO> list = new ArrayList<>();
            //Lấy chi tiết sản phẩm
            DetailProductInfoDTO product = null;
            if(id != null) {
                product = productService.getDetailProductById(id);
                list.add(product);

            }
            else {
                for (int i = 0;i<cartProductService.getAllById(cartid).size();i++) {
                    DetailProductInfoDTO product1 = productService.getDetailProductById(cartProductService.getAllById(cartid).get(i).getProduct().getId());
                    System.out.println(product1);
                    list.add(product1);
                }

                size = cartProductService.getAllById(cartid).get(0).getSize();

            }
            if (size < 35 || size > 42) {
                return "error/404";
            }


            //Lấy danh sách size có sẵn
            List<Integer> availableSizes = productService.getListAvailableSize(id);
            model.addAttribute("availableSizes", availableSizes);
            boolean notFoundSize = true;
            for (Integer availableSize : availableSizes) {
                if (availableSize == size) {
                    notFoundSize = false;
                    break;
                }
            }
            model.addAttribute("notFoundSize", notFoundSize);

            //Lấy danh sách size
            model.addAttribute("sizeVn", SIZE_VN);
            model.addAttribute("sizeUs", SIZE_US);
            model.addAttribute("sizeCm", SIZE_CM);
            model.addAttribute("size", size);
            model.addAttribute("product", list.get(0));
        } catch (NotFoundException ex) {
            return "error/404";
        } catch (Exception ex) {
            return "error/500";
        }


////        Lấy danh sách size có sẵn
//        List<Integer> availableSizes = productService.getListAvailableSize(id);
//        model.addAttribute("availableSizes", availableSizes);
//        boolean notFoundSize = true;
//        for (Integer availableSize : availableSizes) {
//            if (availableSize == size) {
//                notFoundSize = false;
//                break;
//            }
//        }
//        model.addAttribute("notFoundSize", notFoundSize);
//
//        //Lấy danh sách size
//        model.addAttribute("sizeVn", SIZE_VN);
//        model.addAttribute("sizeUs", SIZE_US);
//        model.addAttribute("sizeCm", SIZE_CM);


        return "shop/payment";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
    Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        Cart cart = cartService.findByUserId(user.getId());
        if(cart.isEmpty()) {
            model.addAttribute("emptyCart", true);
//            return "redirect:/shopping-cart/cart";
            return "error/404";
        }
        model.addAttribute("cartItemList", cart.getCartProduct());
        model.addAttribute("shoppingCart", cart);
        model.addAttribute("id", 35);
        if(missingRequiredField) {
            model.addAttribute("missingRequiredField", true);
        }
        return "shop/checkout";
    }

    @GetMapping("/order-submitted/{id}")
    public String orderSubmittedProduct(@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField, Model model, @PathVariable int id, Principal principal){
        if(id!=35){
            User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            OrderDetailDTO order = orderService.userGetDetailById(id, user.getId());
            if (order == null) {
                return "error/404";
            }
            model.addAttribute("order", order);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDateTime = currentDateTime.format(formatter);
            model.addAttribute("createAt",formattedDateTime);
            return "shop/order-submited";
        }
        else{
            String username = principal.getName();
            User user = userService.findByEmail(username);
            Cart cart = cartService.findByUserId(user.getId());
            if(cart.isEmpty()) {
                model.addAttribute("emptyCart", true);
//            return "redirect:/shopping-cart/cart";
                return "error/404";
            }
            model.addAttribute("receiverName", user.getFullName());
            model.addAttribute("cartItemList", cart.getCartProduct());
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("orderId",new Random().nextInt(99) + 1);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDateTime = currentDateTime.format(formatter);
            model.addAttribute("createAt",formattedDateTime);
            if(missingRequiredField) {
                model.addAttribute("missingRequiredField", true);
            }
            return "shop/order-submitted";
        }
    }

    @GetMapping("/order-submitted")
    public String orderSubmittedProducts(@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
                           Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        Cart cart = cartService.findByUserId(user.getId());
        if(cart.isEmpty()) {
            model.addAttribute("emptyCart", true);
//            return "redirect:/shopping-cart/cart";
            return "error/404";
        }
        model.addAttribute("receiverName", user.getFullName());
        model.addAttribute("cartItemList", cart.getCartProduct());
        model.addAttribute("shoppingCart", cart);
        model.addAttribute("orderId",new Random().nextInt(99) + 1);
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateTime = currentDateTime.format(formatter);
        model.addAttribute("createAt",formattedDateTime);
        if(missingRequiredField) {
            model.addAttribute("missingRequiredField", true);
        }
        return "shop/order-submitted";
    }

    @PostMapping("/api/orders")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Order order = orderService.createOrder(createOrderRequest, user.getId());
        return ResponseEntity.ok(order.getId());
    }

    @GetMapping("/products")
    public ResponseEntity<Object> getListBestSellProducts(){
        List<ProductInfoDTO> productInfoDTOS = productService.getListBestSellProducts();
        return ResponseEntity.ok(productInfoDTOS);
    }

    @GetMapping("/san-pham")
    public String getProductShopPages(Model model){

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands",brands);
        List<Long> brandIds = new ArrayList<>();
        for (Brand brand : brands) {
            brandIds.add(brand.getId());
        }
        model.addAttribute("brandIds", brandIds);

        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories",categories);
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        model.addAttribute("categoryIds", categoryIds);

        //Danh sách size của sản phẩm
        model.addAttribute("sizeVn", SIZE_VN);

        //Lấy danh sách sản phẩm
        FilterProductRequest req = new FilterProductRequest(brandIds, categoryIds, new ArrayList<>(), (long) 0, Long.MAX_VALUE, 1);
        PageableDTO result = productService.filterProduct(req);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());

        return "shop/product";
    }

    @PostMapping("/api/san-pham/loc")
    public ResponseEntity<?> filterProduct(@RequestBody FilterProductRequest req) {
        // Validate
        if (req.getMinPrice() == null) {
            req.setMinPrice((long) 0);
        } else {
            if (req.getMinPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }
        if (req.getMaxPrice() == null) {
            req.setMaxPrice(Long.MAX_VALUE);
        } else {
            if (req.getMaxPrice() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mức giá phải lớn hơn 0");
            }
        }

        PageableDTO result = productService.filterProduct(req);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/tim-kiem")
    public String searchProduct(Model model, @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page) {

        PageableDTO result = productService.searchProductByKeyword(keyword, page);

        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("listProduct", result.getItems());
        model.addAttribute("keyword", keyword);
        if (((List<?>) result.getItems()).isEmpty()) {
            model.addAttribute("hasResult", false);
        } else {
            model.addAttribute("hasResult", true);
        }

        return "shop/search";
    }

    @GetMapping("/api/check-hidden-promotion")
    public ResponseEntity<Object> checkPromotion(@RequestParam String code) {
        if (code == null || code == "") {
            throw new BadRequestException("Mã code trống");
        }

        Promotion promotion = promotionService.checkPromotion(code);
        if (promotion == null) {
            throw new BadRequestException("Mã code không hợp lệ");
        }
        CheckPromotion checkPromotion = new CheckPromotion();
        checkPromotion.setDiscountType(promotion.getDiscountType());
        checkPromotion.setDiscountValue(promotion.getDiscountValue());
        checkPromotion.setMaximumDiscountValue(promotion.getMaximumDiscountValue());
        return ResponseEntity.ok(checkPromotion);
    }

    @GetMapping("lien-he")
    public String contact(){
        return "shop/lien-he";
    }
    @GetMapping("huong-dan")
    public String buyGuide(){
        return "shop/buy-guide";
    }
    @GetMapping("chinh-sach")
    public String doiHang(){
        return "shop/chinh-sach";
    }

}
