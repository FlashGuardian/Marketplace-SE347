package com.marketplace.cart.service;

import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.cart.model.CartItem;
import com.marketplace.cart.model.CartItemResponse;
import com.marketplace.cart.model.CartResponse;
import com.marketplace.cart.repository.CartItemRepository;
import com.marketplace.product.model.Product;
import com.marketplace.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User buyer;

    @BeforeEach
    void setUp() {
        buyer = user(1L, "buyer@example.test", "Buyer");
        when(userRepository.findByEmail("buyer@example.test")).thenReturn(Optional.of(buyer));
    }

    @Test
    void addsSellingProductAsSelected() {
        Product product = product(11L, 2L, Product.ProductStatus.SELLING, "125000");
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 11L)).thenReturn(Optional.empty());
        when(cartItemRepository.saveAndFlush(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItemResponse response = cartService.addItem("buyer@example.test", 11L);

        assertTrue(response.selected());
        assertTrue(response.available());
        assertEquals(new BigDecimal("125000"), response.price());
    }

    @Test
    void rejectsDuplicateOwnAndUnavailableProducts() {
        Product product = product(11L, 2L, Product.ProductStatus.SELLING, "100");
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 11L)).thenReturn(Optional.of(new CartItem(1L, 11L)));
        assertEquals(CartException.Code.ALREADY_IN_CART.name(), assertThrows(
                CartException.class, () -> cartService.addItem("buyer@example.test", 11L)
        ).getCode());

        Product ownProduct = product(12L, 1L, Product.ProductStatus.SELLING, "100");
        when(productRepository.findById(12L)).thenReturn(Optional.of(ownProduct));
        assertEquals(CartException.Code.OWN_PRODUCT.name(), assertThrows(
                CartException.class, () -> cartService.addItem("buyer@example.test", 12L)
        ).getCode());

        Product unavailable = product(13L, 2L, Product.ProductStatus.SOLD, "100");
        when(productRepository.findById(13L)).thenReturn(Optional.of(unavailable));
        assertEquals(CartException.Code.PRODUCT_NOT_AVAILABLE.name(), assertThrows(
                CartException.class, () -> cartService.addItem("buyer@example.test", 13L)
        ).getCode());
    }

    @Test
    void mapsMissingProductAndConcurrentUniqueViolation() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertEquals(CartException.Code.PRODUCT_NOT_FOUND.name(), assertThrows(
                CartException.class, () -> cartService.addItem("buyer@example.test", 99L)
        ).getCode());

        Product product = product(11L, 2L, Product.ProductStatus.SELLING, "100");
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 11L)).thenReturn(Optional.empty());
        when(cartItemRepository.saveAndFlush(any(CartItem.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));
        assertEquals(CartException.Code.ALREADY_IN_CART.name(), assertThrows(
                CartException.class, () -> cartService.addItem("buyer@example.test", 11L)
        ).getCode());
    }

    @Test
    void groupsBySellerAndTotalsOnlySelectedAvailableItems() {
        CartItem selected = new CartItem(1L, 11L);
        CartItem unchecked = new CartItem(1L, 12L);
        unchecked.setSelected(false);
        CartItem unavailable = new CartItem(1L, 13L);
        List<CartItem> items = List.of(selected, unchecked, unavailable);
        when(cartItemRepository.findAllByUserIdOrderByAddedAtDescIdDesc(1L)).thenReturn(items);
        List<Product> products = List.of(
                product(11L, 2L, Product.ProductStatus.SELLING, "100"),
                product(12L, 2L, Product.ProductStatus.SELLING, "200"),
                product(13L, 3L, Product.ProductStatus.HELD, "300")
        );
        List<User> sellers = List.of(
                user(2L, "seller1@example.test", "Seller One"),
                user(3L, "seller2@example.test", "Seller Two")
        );
        when(productRepository.findAllById(any())).thenReturn(products);
        when(userRepository.findAllById(any())).thenReturn(sellers);

        CartResponse response = cartService.getCart("buyer@example.test");

        assertEquals(2, response.sellers().size());
        assertEquals("Seller One", response.sellers().get(0).sellerName());
        assertEquals(new BigDecimal("100"), response.selectedTotal());
        assertTrue(response.canCheckout());
        assertFalse(response.sellers().get(1).items().get(0).available());
    }

    @Test
    void selectionIsScopedToTheCurrentUserAndRemovalOnlyDeletesCartItem() {
        when(cartItemRepository.findByUserIdAndProductId(1L, 11L)).thenReturn(Optional.empty());
        assertEquals(CartException.Code.CART_ITEM_NOT_FOUND.name(), assertThrows(
                CartException.class,
                () -> cartService.updateSelection("buyer@example.test", 11L, false)
        ).getCode());
        assertEquals(CartException.Code.CART_ITEM_NOT_FOUND.name(), assertThrows(
                CartException.class, () -> cartService.removeItem("buyer@example.test", 11L)
        ).getCode());

        CartItem item = new CartItem(1L, 11L);
        when(cartItemRepository.findByUserIdAndProductId(1L, 11L)).thenReturn(Optional.of(item));
        Product unavailable = product(11L, 2L, Product.ProductStatus.HELD, "100");
        when(productRepository.findById(11L)).thenReturn(Optional.of(unavailable));
        assertFalse(cartService.updateSelection("buyer@example.test", 11L, false).selected());
        cartService.removeItem("buyer@example.test", 11L);
        verify(cartItemRepository).delete(item);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void emptyCartHasNoCheckoutAndPreviewIncludesUnselectedAndUnavailableItems() {
        when(cartItemRepository.findAllByUserIdOrderByAddedAtDescIdDesc(1L)).thenReturn(List.of());
        when(productRepository.findAllById(any())).thenReturn(List.of());
        CartResponse emptyCart = cartService.getCart("buyer@example.test");
        assertTrue(emptyCart.sellers().isEmpty());
        assertEquals(BigDecimal.ZERO, emptyCart.selectedTotal());
        assertFalse(emptyCart.canCheckout());

        List<CartItem> recentItems = List.of(
                new CartItem(1L, 11L),
                new CartItem(1L, 12L),
                new CartItem(1L, 13L)
        );
        recentItems.get(1).setSelected(false);
        when(cartItemRepository.findTop3ByUserIdOrderByAddedAtDescIdDesc(1L)).thenReturn(recentItems);
        List<Product> products = List.of(
                product(11L, 2L, Product.ProductStatus.SELLING, "100"),
                product(12L, 2L, Product.ProductStatus.HELD, "200"),
                product(13L, 3L, Product.ProductStatus.SOLD, "300")
        );
        when(productRepository.findAllById(any())).thenReturn(products);

        var preview = cartService.getPreview("buyer@example.test");
        assertEquals(List.of(11L, 12L, 13L), preview.items().stream()
                .map(com.marketplace.cart.model.CartPreviewResponse.Item::productId).toList());
    }

    private User user(Long id, String email, String name) {
        User user = new User(name, email, "0900000000", "hash");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Product product(Long id, Long sellerId, Product.ProductStatus status, String price) {
        Product product = org.mockito.Mockito.mock(Product.class);
        lenient().when(product.getId()).thenReturn(id);
        lenient().when(product.getSellerId()).thenReturn(sellerId);
        lenient().when(product.getName()).thenReturn("Product " + id);
        lenient().when(product.getPrice()).thenReturn(new BigDecimal(price));
        lenient().when(product.getStatus()).thenReturn(status);
        return product;
    }
}
