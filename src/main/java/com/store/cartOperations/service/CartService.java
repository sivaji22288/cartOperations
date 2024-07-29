package com.store.cartOperations.service;

import com.store.cartOperations.domain.Cart;
import com.store.cartOperations.domain.Item;
import com.store.cartOperations.domain.Product;
import com.store.cartOperations.repository.CartRepository;
import com.store.cartOperations.repository.ItemRepository;
import com.store.cartOperations.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DiscountService discountService;


    /**
     * This method adds an item to the cart.
     * If the item is already present in the cart, the quantity is updated.
     * If the quantity is less than or equal to 0, an exception is thrown.
     * The total cost of the cart is updated.
     * The discount is applied to the cart and item.
     *
     * @param cartId    The id of the cart.
     * @param productId The id of the product.
     * @param quantity  The quantity of the product.
     * @return The updated cart.
     */
    @Transactional
    public Cart addItem(Integer cartId, Integer productId, Integer quantity) {
        long startTime = System.currentTimeMillis();
        log.info("Adding item to cart with cart id: " + cartId + " and product id: " + productId + " and quantity: " + quantity);
        Cart cart = null;
        try {
            cart = cartRepository.findById(cartId).orElseThrow(() -> {
                log.error("Cart not found with id: " + cartId);
                return new RuntimeException("Cart not found");
            });
            Product product = productRepository.findById(productId).orElseThrow(() -> {
                log.error("Product not found with id: " + productId);
                return new RuntimeException("Product not found");
            });

            if (quantity <= 0) {
                log.error("Quantity should be greater than 0");
                throw new RuntimeException("Quantity should be greater than 0");
            }
            Item item = Item.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .product(product)
                    .category(product.getCategory())
                    .cart(cart)
                    .build();
            List<Item> items = cart.getItems();
            if (items == null) {
                items = new ArrayList<>();
                log.debug("Adding first item to cart with id: " + cartId + " and product id: " + productId + " and quantity: " + quantity);
                items.add(item);
            } else if (items.stream().anyMatch(i -> i.getProduct().getId().equals(productId))) {
                item = items.stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst().get();
                item.setQuantity(item.getQuantity() + quantity);
                log.debug("Updating item in cart with id: " + cartId + " and product id: " + productId + " and quantity: " + item.getQuantity());
            } else {
                log.debug("Adding new item to cart with id: " + cartId + " and product id: " + productId + " and quantity: " + quantity);
                items.add(item);
            }
            item = itemRepository.save(item);
            cart.setItems(items);
            cart.setTotalCost((cart.getTotalCost()!=null?cart.getTotalCost():0.0 )+ (item.getPrice() * quantity));
            cart = cartRepository.save(cart);

            cart = discountService.apply(item);
        } catch (NullPointerException ex) {
            log.error("Cart not found with id: " + cartId);
            log.error(ex.getMessage(),ex);
            throw new RuntimeException("Server error occurred. Please try again later.");
        }
    log.info("Time taken to add item to cart: " + (System.currentTimeMillis() - startTime) + "ms");
    return cart;
    }


    /**
     * This method removes an item from the cart.
     * If the item is not found in the cart, an exception is thrown.
     * The total cost of the cart is updated.
     * The discount is applied to the cart.
     *
     * @param cartId The id of the cart.
     * @param itemId The id of the item.
     * @return The updated cart.
     */
    @Transactional
    public Cart removeItem(Integer cartId, Integer itemId) {

        long startTime = System.currentTimeMillis();
        log.info("Removing item from cart with cart id: " + cartId + " and item id: " + itemId);
        Cart cart = null;
        try {
            cart = cartRepository.findById(cartId).orElseThrow(() -> {
                log.error("Cart not found with id: " + cartId);
                return new RuntimeException("Cart not found");
            });
            Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                log.error("Item not found with id: " + itemId);
                return new RuntimeException("Item not found");
            });

            Integer quantity = item.getQuantity();
            cart.setTotalCost(cart.getTotalCost() - (item.getPrice() * quantity));
            log.debug("Updating total cost of cart with id: " + cartId + " and totalCost: " + cart.getTotalCost());
            cart.getItems().remove(item);
            log.debug("Removing item from cart with id: " + cartId + " and item id: " + itemId);
            itemRepository.delete(item);
            cart = cartRepository.save(cart);
            itemRepository.delete(item);
            cart = discountService.apply(cart);
        }catch (NullPointerException ex) {
            log.error("Cart not found with id: " + cartId);
            log.error(ex.getMessage(),ex);
            throw new RuntimeException("Server error occurred. Please try again later.");
        }
        log.info("Time taken to remove item from cart: " + (System.currentTimeMillis() - startTime) + "ms");
        return cart;
    }

    /**
     * This method updates the quantity of an item in the cart.
     * If the item is not found in the cart, an exception is thrown.
     * If the quantity is less than or equal to 0, an exception is thrown.
     * The total cost of the cart is updated.
     * The discount is applied to the cart.
     *
     * @param itemId   The id of the item.
     * @param quantity The quantity of the item.
     * @return The updated cart.
     */
    @Transactional
    public Cart updateItemQuantity(Integer cartId, Integer itemId, int quantity) {
        long startTime = System.currentTimeMillis();
        log.info("Updating item quantity with item id: " + itemId + " and quantity: " + quantity);
        Cart cart = null;
        try {
            Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                log.error("Item not found with id: " + itemId);
                return new RuntimeException("Item not found");
            });
            Integer previousQuantity = item.getQuantity();
            cart = cartRepository.findById(cartId).orElseThrow(() -> {
                log.error("Cart not found with id: " + cartId);
                return new RuntimeException("Cart not found");
            });
            if (quantity <= 0) {
                log.error("Quantity should be greater than 0");
                throw new RuntimeException("Quantity should be greater than 0");
            }
            Integer quantityDifference = quantity - previousQuantity;
            log.debug("Quantity difference - " + quantityDifference);
            cart.setTotalCost(cart.getTotalCost() + (item.getPrice() * quantityDifference));
            log.debug("Updating total cost of cart with id: " + cart.getId() + " and totalCost: " + cart.getTotalCost());
            item.setQuantity(quantity);
            item = itemRepository.save(item);
            cart = discountService.apply(item);
            cart = cartRepository.save(cart);
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(),ex);
            throw new RuntimeException("Server error occurred. Please try again later.");
        }
        log.info("Time taken to update item quantity: " + (System.currentTimeMillis() - startTime) + "ms");
        return cart;
    }

    /**
     * This method returns the cart with the given id.
     * If the cart is not found, an exception is thrown.
     *
     * @param cartId The id of the cart.
     * @return The cart.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Cart getCart(Integer cartId) {
        long startTime = System.currentTimeMillis();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> {
            log.error("Cart not found with id: " + cartId);
            return new RuntimeException("Cart not found");
        });
        log.info("Time taken to get cart: " + (System.currentTimeMillis() - startTime) + "ms");
        return cart;
    }
}
