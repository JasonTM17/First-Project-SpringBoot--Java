package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.OrderStatus;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.service.specification.ProductSpecs;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService,
            OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {

        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
    }

    public Product createProduct(Product pr) {
        return this.productRepository.save(pr);
    }

    public Page<Product> fetchProducts(Pageable page) {
        return this.productRepository.findAll(page);
    }

    public Page<Product> searchAdminProducts(Pageable page, String keyword, String factory) {
        return this.productRepository.searchAdminProducts(normalizeFilter(keyword), normalizeFilter(factory), page);
    }

    public List<Product> fetchProductsByFactory(String factory, int limit) {
        ProductCriteriaDTO criteria = new ProductCriteriaDTO();
        criteria.setFactory(Optional.of(List.of(factory)));
        return this.fetchProductsWithSpec(PageRequest.of(0, Math.max(1, limit)), criteria).getContent();
    }

    public List<Product> fetchProductsByTarget(String target, int limit) {
        ProductCriteriaDTO criteria = new ProductCriteriaDTO();
        criteria.setTarget(Optional.of(List.of(target)));
        return this.fetchProductsWithSpec(PageRequest.of(0, Math.max(1, limit)), criteria).getContent();
    }

    public List<Product> fetchProductsByPriceRange(String price, int limit) {
        ProductCriteriaDTO criteria = new ProductCriteriaDTO();
        criteria.setPrice(Optional.of(List.of(price)));
        return this.fetchProductsWithSpec(PageRequest.of(0, Math.max(1, limit)), criteria).getContent();
    }

    public List<Product> suggestProducts(String keyword, int limit) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }
        Pageable page = PageRequest.of(0, Math.max(1, Math.min(limit, 8)), Sort.by(Sort.Direction.DESC, "sold"));
        return this.productRepository.findByNameContainingIgnoreCase(keyword.trim(), page).getContent();
    }

    public Page<Product> fetchProductsWithSpec(Pageable page, ProductCriteriaDTO productCriteriaDTO) {
        Optional<String> name = Optional.ofNullable(productCriteriaDTO.getName()).orElse(Optional.empty());
        Optional<List<String>> target = Optional.ofNullable(productCriteriaDTO.getTarget()).orElse(Optional.empty());
        Optional<List<String>> factory = Optional.ofNullable(productCriteriaDTO.getFactory()).orElse(Optional.empty());
        Optional<List<String>> price = Optional.ofNullable(productCriteriaDTO.getPrice()).orElse(Optional.empty());

        if (name.isEmpty() && target.isEmpty() && factory.isEmpty() && price.isEmpty()) {
            return this.productRepository.findAll(page);
        }
        Specification<Product> combinedSpec = Specification.unrestricted();

        if (name.isPresent()) {
            String keyword = name.get().trim();
            if (!keyword.isBlank()) {
                combinedSpec = combinedSpec.and(ProductSpecs.nameLike(keyword));
            }
        }

        if (target.isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListTarget(target.get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }
        if (factory.isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListFactory(factory.get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (price.isPresent()) {
            Specification<Product> currentSpecs = this.buildPriceSpecification(price.get());

            combinedSpec = combinedSpec.and(currentSpecs);
        }

        return this.productRepository.findAll(combinedSpec, page);
    }

    public Specification<Product> buildPriceSpecification(List<String> price) {
        Specification<Product> combinedSpec = Specification.unrestricted();
        for (String p : price) {
            double min = 0;
            double max = 0;
            switch (p) {
                case "duoi-10-trieu":
                    min = 1;
                    max = 10000000;
                    break;
                case "10-toi-15-trieu":
                    min = 10000000;
                    max = 15000000;

                    break;
                case "15-toi-20-trieu":
                    min = 15000000;
                    max = 20000000;

                    break;
                case "tren-20-trieu":
                    min = 20000000;
                    max = 200000000;
                    break;
            }
            if (min != 0 && max != 0) {
                Specification<Product> rangeSpec = ProductSpecs.matchPrice(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            }
        }

        return combinedSpec;
    }

    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    @Transactional
    public int handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1");
        }

        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng");
        }

        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) {
            Cart otherCart = new Cart();
            otherCart.setUser(user);
            otherCart.setSum(0);
            cart = this.cartRepository.save(otherCart);
        }

        Product realProduct = this.productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm"));
        CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
        long nextQuantity = oldDetail == null ? quantity : oldDetail.getQuantity() + quantity;

        if (realProduct.getQuantity() < nextQuantity) {
            throw new IllegalArgumentException("Số lượng trong kho không đủ");
        }

        if (oldDetail == null) {
            CartDetail cd = new CartDetail();
            cd.setCart(cart);
            cd.setProduct(realProduct);
            cd.setPrice(realProduct.getPrice());
            cd.setQuantity(quantity);
            this.cartDetailRepository.save(cd);

            int s = cart.getSum() + 1;
            cart.setSum(s);
            this.cartRepository.save(cart);
            setCartCount(session, s);
            return s;
        }

        oldDetail.setQuantity(nextQuantity);
        this.cartDetailRepository.save(oldDetail);
        setCartCount(session, cart.getSum());
        return cart.getSum();
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUserIdWithDetails(user.getId());
    }

    @Transactional
    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        handleRemoveCartDetail(cartDetailId, 0, session);
    }

    @Transactional
    public void handleRemoveCartDetail(long cartDetailId, long userId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            ensureCartDetailBelongsToUser(cartDetail, userId);
            Cart currentCart = cartDetail.getCart();
            // delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailId);

            // update cart
            if (currentCart.getSum() > 1) {
                // update current cart
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                setCartCount(session, s);
                this.cartRepository.save(currentCart);
            } else {
                // delete cart (sum = 1)
                detachCartFromUser(currentCart);
                this.cartRepository.deleteById(currentCart.getId());
                setCartCount(session, 0);
            }
        }
    }

    @Transactional
    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        handleUpdateCartBeforeCheckout(cartDetails, 0);
    }

    @Transactional
    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails, long userId) {
        if (cartDetails == null) {
            return;
        }
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                ensureCartDetailBelongsToUser(currentCartDetail, userId);
                if (cartDetail.getQuantity() < 1) {
                    throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1");
                }
                Product product = currentCartDetail.getProduct();
                if (product == null || product.getQuantity() < cartDetail.getQuantity()) {
                    throw new IllegalArgumentException("Số lượng trong kho không đủ");
                }
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    private void ensureCartDetailBelongsToUser(CartDetail cartDetail, long userId) {
        if (userId <= 0) {
            return;
        }
        Cart cart = cartDetail == null ? null : cartDetail.getCart();
        User owner = cart == null ? null : cart.getUser();
        if (owner == null || owner.getId() != userId) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng của bạn");
        }
    }

    @Transactional
    public boolean handlePlaceOrder(User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone, String paymentMethod) {
        User managedUser = user == null ? null : this.userService.getUserById(user.getId());
        if (managedUser == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản đặt hàng");
        }

        Cart cart = this.cartRepository.findByUser(managedUser);
        if (cart == null || cart.getCartDetails() == null || cart.getCartDetails().isEmpty()) {
            return false;
        }

        List<CartDetail> cartDetails = cart.getCartDetails();
        double sum = 0;
        for (CartDetail cd : cartDetails) {
            if (cd.getQuantity() < 1) {
                throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1");
            }
            Product product = cd.getProduct();
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm trong giỏ hàng không còn tồn tại");
            }
            Product currentProduct = this.productRepository.findById(product.getId())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm"));
            if (currentProduct.getQuantity() < cd.getQuantity()) {
                throw new IllegalArgumentException("Số lượng trong kho không đủ cho " + currentProduct.getName());
            }
            sum += cd.getPrice() * cd.getQuantity();
        }

        Order order = new Order();
        order.setUser(managedUser);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentMethod(paymentMethod == null || paymentMethod.isBlank() ? "COD" : paymentMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(sum);
        order = this.orderRepository.save(order);

        for (CartDetail cd : cartDetails) {
            Product currentProduct = this.productRepository.findById(cd.getProduct().getId())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(currentProduct);
            orderDetail.setPrice(cd.getPrice());
            orderDetail.setQuantity(cd.getQuantity());
            this.orderDetailRepository.save(orderDetail);

            currentProduct.setQuantity(currentProduct.getQuantity() - cd.getQuantity());
            currentProduct.setSold(currentProduct.getSold() + cd.getQuantity());
            this.productRepository.save(currentProduct);
        }

        for (CartDetail cd : cartDetails) {
            this.cartDetailRepository.deleteById(cd.getId());
        }
        detachCartFromUser(cart);
        this.cartRepository.deleteById(cart.getId());
        setCartCount(session, 0);
        return true;
    }

    private void detachCartFromUser(Cart cart) {
        if (cart == null) {
            return;
        }
        User cartUser = cart.getUser();
        if (cartUser != null && cartUser.getCart() == cart) {
            cartUser.setCart(null);
        }
        cart.setUser(null);
    }

    private void setCartCount(HttpSession session, int count) {
        if (session != null) {
            session.setAttribute("sum", count);
        }
    }

    private String normalizeFilter(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

}
