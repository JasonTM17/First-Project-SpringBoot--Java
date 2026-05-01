package vn.hoidanit.laptopshop.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Role;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.repository.RoleRepository;
import vn.hoidanit.laptopshop.repository.UserRepository;

@SpringBootTest(properties = "spring.session.store-type=none")
@AutoConfigureMockMvc
class LaptopshopE2eTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartDetailRepository cartDetailRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager entityManager;

    Product appleLaptop;
    Product dellLaptop;
    User customer;

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        cartDetailRepository.deleteAllInBatch();
        cartRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        entityManager.clear();

        Role userRole = roleRepository.save(role("USER", "End user"));
        roleRepository.save(role("ADMIN", "Administrator"));

        customer = new User();
        customer.setEmail("e2e.customer@example.test");
        customer.setPassword(passwordEncoder.encode("Password123!"));
        customer.setFullName("E2E Customer");
        customer.setPhone("0900000000");
        customer.setAddress("123 Test Street, Ha Noi");
        customer.setAvatar("default.png");
        customer.setRole(userRole);
        customer = userRepository.save(customer);

        appleLaptop = productRepository.save(product(
                "MacBook Air E2E",
                26990000,
                "1711079954090-apple-01.png",
                "APPLE",
                "MONG-NHE",
                5));
        dellLaptop = productRepository.save(product(
                "Dell Inspiron E2E",
                14990000,
                "1711078452562-dell-01.png",
                "DELL",
                "SINHVIEN-VANPHONG",
                6));
    }

    @Test
    void storefrontCatalogAndErrorPagesRenderExpectedViews() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/homepage/show"))
                .andExpect(model().attributeExists("products"));

        mockMvc.perform(get("/products").param("factory", "APPLE"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/product/show"))
                .andExpect(model().attribute("totalProducts", 1L))
                .andExpect(model().attribute("products", hasSize(1)));

        mockMvc.perform(get("/products").param("name", "Inspiron"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/product/show"))
                .andExpect(model().attribute("totalProducts", 1L))
                .andExpect(model().attribute("products", hasSize(1)));

        mockMvc.perform(get("/products")
                .param("price", "duoi-10-trieu")
                .param("price", "10-toi-15-trieu"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/product/show"))
                .andExpect(model().attribute("totalProducts", 1L))
                .andExpect(model().attribute("products", hasSize(1)));

        mockMvc.perform(get("/product/{id}", appleLaptop.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("client/product/detail"))
                .andExpect(model().attributeExists("product"));

        mockMvc.perform(get("/product/{id}", 999_999L))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/site.webmanifest"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Laptopshop")));

        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/products")));

        mockMvc.perform(get("/robots.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Disallow: /admin/")));

        mockMvc.perform(get("/robots.txt").header("Host", "review.test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sitemap: http://review.test/sitemap.xml")));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/auth/register"));

        mockMvc.perform(post("/register")
                .with(csrf())
                .param("firstName", "New")
                .param("lastName", "Reviewer")
                .param("email", "new.reviewer@example.test")
                .param("password", "Password123!")
                .param("confirmPassword", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registerSuccess"));

        mockMvc.perform(get("/api/products/suggest").param("name", "Mac"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("MacBook Air E2E"));

        mockMvc.perform(get("/images/branding/laptopshop-favicon.svg"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Laptopshop favicon")));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UP")));

        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Laptopshop")));
    }

    @Test
    void unauthenticatedApiAndProtectedPagesUseCorrectEntryPoints() throws Exception {
        mockMvc.perform(post("/api/add-product-to-cart")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":" + appleLaptop.getId() + ",\"quantity\":1}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("đăng nhập")));

        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/admin").with(user(customer.getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedCustomerCanAddCartCheckoutAndPlaceOrder() throws Exception {
        MockHttpSession session = customerSession();

        mockMvc.perform(post("/api/add-product-to-cart")
                .with(csrf())
                .with(user(customer.getEmail()).roles("USER"))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":" + appleLaptop.getId() + ",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartCount").value(1));

        assertThat(cartRepository.findByUserId(customer.getId()).getSum()).isEqualTo(1);

        mockMvc.perform(get("/cart")
                .with(user(customer.getEmail()).roles("USER"))
                .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("client/cart/show"))
                .andExpect(model().attributeExists("cartDetails", "totalPrice", "cart"));

        mockMvc.perform(post("/place-order")
                .with(csrf())
                .with(user(customer.getEmail()).roles("USER"))
                .session(session)
                .param("receiverName", "E2E Customer")
                .param("receiverAddress", "123 Test Street, Ha Noi")
                .param("receiverPhone", "0900000000")
                .param("paymentMethod", "COD"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/thanks"));

        List<Order> orders = orderRepository.findByUser(customer);
        Product orderedProduct = productRepository.findById(appleLaptop.getId()).orElseThrow();

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo("PENDING");
        assertThat(orders.get(0).getPaymentMethod()).isEqualTo("COD");
        assertThat(orders.get(0).getTotalPrice()).isEqualTo(appleLaptop.getPrice() * 2);
        assertThat(orderedProduct.getQuantity()).isEqualTo(3);
        assertThat(orderedProduct.getSold()).isEqualTo(2);
        assertThat(cartRepository.findByUserId(customer.getId())).isNull();
        assertThat(session.getAttribute("sum")).isEqualTo(0);

        MvcResult historyResult = mockMvc.perform(get("/order-history")
                .with(user(customer.getEmail()).roles("USER"))
                .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("client/cart/order-history"))
                .andExpect(model().attributeExists("orders"))
                .andReturn();
        @SuppressWarnings("unchecked")
        List<Order> historyOrders = (List<Order>) historyResult.getModelAndView().getModel().get("orders");
        assertThat(historyOrders).hasSize(1);
        assertThat(historyOrders.get(0).getOrderDetails()).hasSize(1);
        assertThat(historyOrders.get(0).getOrderDetails().get(0).getProduct().getName()).isEqualTo(appleLaptop.getName());

        mockMvc.perform(get("/admin/order/{id}", orders.get(0).getId())
                .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/detail"))
                .andExpect(model().attributeExists("orderDetails"));

        mockMvc.perform(post("/admin/order/update")
                .with(csrf())
                .with(user("admin@example.test").roles("ADMIN"))
                .param("id", String.valueOf(orders.get(0).getId()))
                .param("status", "SHIPPING"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/order/update/" + orders.get(0).getId()));

        mockMvc.perform(post("/admin/order/update")
                .with(csrf())
                .with(user("admin@example.test").roles("ADMIN"))
                .param("id", String.valueOf(orders.get(0).getId()))
                .param("status", "CONFIRMED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/order"));
    }

    @Test
    void adminDashboardIsAvailableForAdminRole() throws Exception {
        Order pendingOrder = new Order();
        pendingOrder.setUser(customer);
        pendingOrder.setReceiverName("E2E Customer");
        pendingOrder.setReceiverPhone("0900000000");
        pendingOrder.setReceiverAddress("123 Test Street, Ha Noi");
        pendingOrder.setPaymentMethod("COD");
        pendingOrder.setStatus("PENDING");
        pendingOrder.setTotalPrice(dellLaptop.getPrice());
        orderRepository.save(pendingOrder);

        mockMvc.perform(get("/admin").with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard/show"))
                .andExpect(model().attribute("countUsers", 1L))
                .andExpect(model().attribute("countProducts", 2L))
                .andExpect(model().attributeExists("recentOrders", "totalRevenue", "revenueLabelsJson", "factoryLabelsJson"));

        mockMvc.perform(get("/admin/report/orders.csv").with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id,customer_email,customer_name,total_price,status")));

        mockMvc.perform(get("/admin/product")
                .param("q", "Inspiron")
                .param("factory", "DELL")
                .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/show"))
                .andExpect(model().attribute("products", hasSize(1)))
                .andExpect(model().attribute("query", "Inspiron"))
                .andExpect(model().attribute("factoryFilter", "DELL"));

        mockMvc.perform(get("/admin/user")
                .param("q", "e2e.customer")
                .param("role", "USER")
                .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user/show"))
                .andExpect(model().attribute("users1", hasSize(1)))
                .andExpect(model().attribute("query", "e2e.customer"))
                .andExpect(model().attribute("roleFilter", "USER"));

        mockMvc.perform(get("/admin/order")
                .param("q", "E2E Customer")
                .param("status", "PENDING")
                .with(user("admin@example.test").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order/show"))
                .andExpect(model().attribute("orders", hasSize(1)))
                .andExpect(model().attribute("query", "E2E Customer"))
                .andExpect(model().attribute("statusFilter", "PENDING"));
    }

    private MockHttpSession customerSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);
        session.setAttribute("fullName", customer.getFullName());
        session.setAttribute("avatar", customer.getAvatar());
        session.setAttribute("id", customer.getId());
        session.setAttribute("email", customer.getEmail());
        session.setAttribute("sum", 0);
        return session;
    }

    private MockHttpSession loginAsCustomer() throws Exception {
        MvcResult result = mockMvc.perform(formLogin("/login")
                .user(customer.getEmail())
                .password("Password123!"))
                .andExpect(authenticated().withUsername(customer.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private Role role(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }

    private Product product(String name, double price, String image, String factory, String target, long quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImage(image);
        product.setDetailDesc("E2E detail for " + name);
        product.setShortDesc("E2E short description");
        product.setFactory(factory);
        product.setTarget(target);
        product.setQuantity(quantity);
        product.setSold(0);
        return product;
    }
}
