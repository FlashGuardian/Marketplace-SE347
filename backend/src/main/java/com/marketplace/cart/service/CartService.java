package com.marketplace.cart.service;

import com.marketplace.auth.model.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.cart.model.CartItem;
import com.marketplace.cart.model.CartItemResponse;
import com.marketplace.cart.model.CartPreviewResponse;
import com.marketplace.cart.model.CartResponse;
import com.marketplace.cart.model.CartSellerGroup;
import com.marketplace.cart.repository.CartItemRepository;
import com.marketplace.product.model.Product;
import com.marketplace.product.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartItemResponse addItem(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CartException(CartException.Code.PRODUCT_NOT_FOUND));

        if (product.getSellerId().equals(user.getId())) {
            throw new CartException(CartException.Code.OWN_PRODUCT);
        }
        if (product.getStatus() != Product.ProductStatus.SELLING) {
            throw new CartException(CartException.Code.PRODUCT_NOT_AVAILABLE);
        }
        if (cartItemRepository.findByUserIdAndProductId(user.getId(), productId).isPresent()) {
            throw new CartException(CartException.Code.ALREADY_IN_CART);
        }

        CartItem cartItem = new CartItem(user.getId(), productId);
        try {
            cartItemRepository.saveAndFlush(cartItem);
        } catch (DataIntegrityViolationException exception) {
            throw new CartException(CartException.Code.ALREADY_IN_CART);
        }
        return toResponse(cartItem, product);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        User user = getUser(email);
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByAddedAtDescIdDesc(user.getId());
        Map<Long, Product> products = productRepository.findAllById(
                cartItems.stream().map(CartItem::getProductId).distinct().toList()
        ).stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<Long> sellerIds = cartItems.stream()
                .map(item -> requireProduct(products, item.getProductId()).getSellerId())
                .distinct()
                .toList();
        Map<Long, User> sellers = userRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, List<CartItemResponse>> grouped = new LinkedHashMap<>();
        BigDecimal selectedTotal = BigDecimal.ZERO;
        boolean canCheckout = false;

        for (CartItem cartItem : cartItems) {
            Product product = requireProduct(products, cartItem.getProductId());
            boolean available = product.getStatus() == Product.ProductStatus.SELLING;
            grouped.computeIfAbsent(product.getSellerId(), ignored -> new ArrayList<>())
                    .add(toResponse(cartItem, product));
            if (cartItem.isSelected() && available) {
                selectedTotal = selectedTotal.add(product.getPrice());
                canCheckout = true;
            }
        }

        List<CartSellerGroup> groups = grouped.entrySet().stream()
                .map(entry -> {
                    User seller = sellers.get(entry.getKey());
                    if (seller == null) {
                        throw new IllegalStateException("Product seller does not exist: " + entry.getKey());
                    }
                    return new CartSellerGroup(entry.getKey(), seller.getFullName(), List.copyOf(entry.getValue()));
                })
                .toList();

        return new CartResponse(groups, selectedTotal, canCheckout);
    }

    @Transactional
    public CartItemResponse updateSelection(String email, Long productId, boolean selected) {
        User user = getUser(email);
        CartItem cartItem = getCartItem(user.getId(), productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Cart item references a missing product: " + productId));
        cartItem.setSelected(selected);
        cartItemRepository.save(cartItem);
        return toResponse(cartItem, product);
    }

    @Transactional
    public void removeItem(String email, Long productId) {
        User user = getUser(email);
        cartItemRepository.delete(getCartItem(user.getId(), productId));
    }

    @Transactional(readOnly = true)
    public CartPreviewResponse getPreview(String email) {
        User user = getUser(email);
        List<CartItem> cartItems = cartItemRepository.findTop3ByUserIdOrderByAddedAtDescIdDesc(user.getId());
        Map<Long, Product> products = productRepository.findAllById(
                cartItems.stream().map(CartItem::getProductId).distinct().toList()
        ).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        return new CartPreviewResponse(cartItems.stream()
                .map(item -> {
                    Product product = requireProduct(products, item.getProductId());
                    return new CartPreviewResponse.Item(product.getId(), product.getName(), product.getPrice());
                })
                .toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user does not exist"));
    }

    private CartItem getCartItem(Long userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new CartException(CartException.Code.CART_ITEM_NOT_FOUND));
    }

    private Product requireProduct(Map<Long, Product> products, Long productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new IllegalStateException("Cart item references a missing product: " + productId);
        }
        return product;
    }

    private CartItemResponse toResponse(CartItem cartItem, Product product) {
        return new CartItemResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.isSelected(),
                product.getStatus() == Product.ProductStatus.SELLING,
                cartItem.getAddedAt()
        );
    }
}
