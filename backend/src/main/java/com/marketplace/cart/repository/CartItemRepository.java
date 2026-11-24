package com.marketplace.cart.repository;

import com.marketplace.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    List<CartItem> findAllByUserIdOrderByAddedAtDescIdDesc(Long userId);

    List<CartItem> findTop3ByUserIdOrderByAddedAtDescIdDesc(Long userId);
}
