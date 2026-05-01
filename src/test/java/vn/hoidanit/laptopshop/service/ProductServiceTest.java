package vn.hoidanit.laptopshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    CartDetailRepository cartDetailRepository;

    @Mock
    UserService userService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @InjectMocks
    ProductService productService;

    @Test
    void addProductToCartCreatesCartDetailAndSyncsSessionCount() {
        User user = new User();
        user.setEmail("buyer@example.com");

        Product product = new Product();
        product.setId(10);
        product.setPrice(1200);
        product.setQuantity(5);

        when(userService.getUserByEmail("buyer@example.com")).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartAndProduct(any(Cart.class), any(Product.class))).thenReturn(null);

        HttpSession session = mock(HttpSession.class);
        int cartCount = productService.handleAddProductToCart("buyer@example.com", 10, session, 2);

        ArgumentCaptor<CartDetail> detailCaptor = ArgumentCaptor.forClass(CartDetail.class);
        verify(cartDetailRepository).save(detailCaptor.capture());
        CartDetail savedDetail = detailCaptor.getValue();

        assertThat(cartCount).isEqualTo(1);
        assertThat(savedDetail.getProduct()).isSameAs(product);
        assertThat(savedDetail.getQuantity()).isEqualTo(2);
        assertThat(savedDetail.getPrice()).isEqualTo(1200);
        verify(session).setAttribute("sum", 1);
    }

    @Test
    void addProductToCartRejectsInvalidQuantity() {
        HttpSession session = mock(HttpSession.class);

        assertThatThrownBy(() -> productService.handleAddProductToCart("buyer@example.com", 10, session, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Số lượng");
    }

    @Test
    void addProductToCartRejectsQuantityAboveStock() {
        User user = new User();
        Product product = new Product();
        product.setId(10);
        product.setQuantity(1);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setSum(1);

        when(userService.getUserByEmail("buyer@example.com")).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        HttpSession session = mock(HttpSession.class);

        assertThatThrownBy(() -> productService.handleAddProductToCart("buyer@example.com", 10, session, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("kho");
    }

    @Test
    void placeOrderReturnsFalseForEmptyCart() {
        User user = new User();
        user.setId(1);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartDetails(List.of());

        when(userService.getUserById(1)).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(cart);

        boolean placed = productService.handlePlaceOrder(user, mock(HttpSession.class), "A", "Address", "0900000000", "COD");

        assertThat(placed).isFalse();
    }

    @Test
    void placeOrderStoresPaymentMethodAndUpdatesStock() {
        User user = new User();
        user.setId(1);

        Product product = new Product();
        product.setId(10);
        product.setName("Laptop");
        product.setQuantity(5);
        product.setSold(1);

        Cart cart = new Cart();
        cart.setId(99);
        cart.setUser(user);

        CartDetail detail = new CartDetail();
        detail.setId(50);
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setPrice(1000);
        detail.setQuantity(2);
        cart.setCartDetails(List.of(detail));

        when(userService.getUserById(1)).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean placed = productService.handlePlaceOrder(user, mock(HttpSession.class), "Buyer", "Address 123", "0900000000", "BANK");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertThat(placed).isTrue();
        assertThat(orderCaptor.getValue().getPaymentMethod()).isEqualTo("BANK");
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualTo(2000);
        assertThat(product.getQuantity()).isEqualTo(3);
        assertThat(product.getSold()).isEqualTo(3);
    }

    @Test
    void removeCartDetailRejectsDifferentCartOwner() {
        User owner = new User();
        owner.setId(2);

        Cart cart = new Cart();
        cart.setUser(owner);
        cart.setSum(1);

        CartDetail detail = new CartDetail();
        detail.setId(50);
        detail.setCart(cart);

        when(cartDetailRepository.findById(50L)).thenReturn(Optional.of(detail));

        assertThatThrownBy(() -> productService.handleRemoveCartDetail(50, 1, mock(HttpSession.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("giỏ hàng");
        verify(cartDetailRepository, never()).deleteById(50L);
    }

    @Test
    void updateCartBeforeCheckoutRejectsDifferentCartOwner() {
        User owner = new User();
        owner.setId(2);

        Cart cart = new Cart();
        cart.setUser(owner);

        CartDetail existing = new CartDetail();
        existing.setId(50);
        existing.setCart(cart);

        CartDetail posted = new CartDetail();
        posted.setId(50);
        posted.setQuantity(1);

        when(cartDetailRepository.findById(50L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productService.handleUpdateCartBeforeCheckout(List.of(posted), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("giỏ hàng");
        verify(cartDetailRepository, never()).save(any(CartDetail.class));
    }
}
