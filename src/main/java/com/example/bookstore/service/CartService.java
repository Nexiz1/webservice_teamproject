package com.example.bookstore.service;

import com.example.bookstore.dto.cart.CartItemRequest;
import com.example.bookstore.dto.cart.CartItemResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    // Get or create cart for user
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Long addCartItem(User user, CartItemRequest request) {
        Cart cart = getOrCreateCart(user);

        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        // Check if item already exists
        CartItem existingItem = cartItemRepository.findByCartAndBookAndDeletedFalse(cart, book)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
            return cart.getId();
        }

        // Create new cart item
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(request.getQuantity())
                .build();
        cartItemRepository.save(cartItem);

        return cart.getId();
    }

    @Transactional
    public Long updateCartItemQuantity(User user, CartItemRequest request) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartAndBookAndDeletedFalse(cart, book)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return cart.getId();
    }

    public List<CartItemResponse> getCartItems(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElse(null);

        if (cart == null) {
            return List.of();
        }

        return cartItemRepository.findByCartAndDeletedFalse(cart).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCartItem(User user, Long cartItemId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByIdAndDeletedFalse(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Verify ownership
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // Soft delete
        cartItem.setDeleted(true);
        cartItemRepository.save(cartItem);
    }
}
