package com.store.cartOperations.service;

import com.store.cartOperations.domain.*;
import com.store.cartOperations.repository.CartRepository;
import com.store.cartOperations.repository.ItemRepository;
import com.store.cartOperations.repository.ProductRepository;
import com.store.cartOperations.repository.RetailUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class CartServiceTest {

    @Autowired
    CartService cartService;

    @Autowired
    RetailUserRepository retailUserRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ItemRepository itemRepository;

    @DisplayName("Test Add Item to Cart")
    @Test
    void testAddItem() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(true).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .build();
        cart = cartRepository.save(cart);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);
        // When
        Cart updatedCart = cartService.addItem(cart.getId(), product.getId(), 2);

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(1, updatedCart.getItems().size());
        Assertions.assertEquals(2, updatedCart.getItems().get(0).getQuantity());
        Assertions.assertEquals(200.0, updatedCart.getTotalCost());
        Assertions.assertEquals(10, updatedCart.getBillDiscount());
        Assertions.assertEquals(60, updatedCart.getItems().get(0).getDiscount());
    }

    @DisplayName("Test Add Item to Cart with existing item")
    @Test
    void testAddItemToExistingItem() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);
        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item);
        item.setCart(cart);
        cart = cartRepository.save(cart);


        // When
        Cart updatedCart = cartService.addItem(cart.getId(), product.getId(), 2);

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(1, updatedCart.getItems().size());
        Assertions.assertEquals(4, updatedCart.getItems().get(0).getQuantity());
    }

    @DisplayName("Test Add Item to Cart with new item")
    @Test
    void testAddNewItemToExistingItems() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);
        Product product1 = Product.builder()
                .id(null)
                .name("Product 2")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.GROCERY)
                .build();
        product1 = productRepository.save(product1);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item);
        cart = cartRepository.save(cart);


        // When
        Cart updatedCart = cartService.addItem(cart.getId(), product1.getId(), 2);

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(2, updatedCart.getItems().size());
    }

    @DisplayName("Test add Item to Cart with In valid cartId")
    @Test
    void testAddItemToInvalidCart() {
        // Given
        Integer cartId = 100;
        Integer productId = 1;
        Integer quantity = 2;

        // When
        Assertions.assertThrows(RuntimeException.class, () -> cartService.addItem(cartId, productId, quantity));
    }

    @DisplayName("Test add Item to Cart with In valid productId")
    @Test
    void testAddItemWithInvalidProduct() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        Integer productId = 1;
        Integer quantity = 2;

        // When
        Cart finalCart = cart;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.addItem(finalCart.getId(), productId, quantity));
    }

    @DisplayName("Test add Item to Cart with negative quantity")
    @Test
    void testAddItemWithNegativeQuantity() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);
        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);
        Integer quantity = -2;

        // When
        Cart finalCart = cart;
        Product finalProduct = product;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.addItem(finalCart.getId(), finalProduct.getId(), quantity));
    }

    @DisplayName("Test add Item to Cart with zero quantity")
    @Test
    void testAddItemWithZeroQuantity() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);
        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);
        Integer quantity = 0;

        // When
        Cart finalCart = cart;
        Product finalProduct = product;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.addItem(finalCart.getId(), finalProduct.getId(), quantity));
    }


    @DisplayName("Test update Item quantity to Cart by decreasing quantity")
    @Test
    void testUpdateItemQuantityToCart() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(true).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(4)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .totalCost(400.0)
                .build();
        cart.getItems().add(item);
        item.setCart(cart);
        cart = cartRepository.save(cart);


        // When
        Cart updatedCart = cartService.updateItemQuantity(cart.getId(),item.getId(), 2);

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(1, updatedCart.getItems().size());
        Assertions.assertEquals(2, updatedCart.getItems().get(0).getQuantity());
        Assertions.assertEquals(200.0, updatedCart.getTotalCost());
        Assertions.assertEquals(10, updatedCart.getBillDiscount());
        Assertions.assertEquals(60, updatedCart.getItems().get(0).getDiscount());
    }

    @DisplayName("Test Update Item Quantity in Cart by increasing quantity")
    @Test
    void testUpdateItemQuantity() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart.setTotalCost(200.0);
        cart.getItems().add(item);
        item.setCart(cart);
        cart = cartRepository.save(cart);

        // When
        Cart updatedCart = cartService.updateItemQuantity(cart.getId(),item.getId(), 3);

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(1, updatedCart.getItems().size());
        Assertions.assertEquals(3, updatedCart.getItems().get(0).getQuantity());
        Assertions.assertEquals(300.0, updatedCart.getTotalCost());
        Assertions.assertEquals(15, updatedCart.getBillDiscount());
        Assertions.assertEquals(0, updatedCart.getItems().get(0).getDiscount());
    }

    @DisplayName("Test Update Item Quantity in Cart with invalid itemId")
    @Test
    void testUpdateItemQuantityWithInvalidItem() {
        // Given
        Integer cartId = 10;
        Integer itemId = 100;
        Integer quantity = 3;

        // When
        Assertions.assertThrows(RuntimeException.class, () -> cartService.updateItemQuantity(cartId,itemId, quantity));
    }

    @DisplayName("Test Update Item Quantity in Cart with zero quantity")
    @Test
    void testUpdateItemQuantityWithZeroQuantity() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isAffiliated(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item);
        cart = cartRepository.save(cart);

        Integer quantity = 0;

        // When
        Item finalItem = item;
        Cart finalCart = cart;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.updateItemQuantity(finalCart.getId(),finalItem.getId(), quantity));
    }

    @DisplayName("Test Update Item Quantity in Cart with item not attached to cart")
    @Test
    void testUpdateItemQuantityWithItemNotAttachedToCart() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isAffiliated(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        //cart.getItems().add(item);
        cart = cartRepository.save(cart);

        Integer quantity = 0;

        // When
        Item finalItem = item;
        Cart finalCart = cart;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.updateItemQuantity(finalCart.getId(),finalItem.getId(), quantity));
    }

    @DisplayName("Test Update Item Quantity in Cart with negative quantity")
    @Test
    void testUpdateItemQuantityWithNegativeQuantity() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isAffiliated(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item);
        cart = cartRepository.save(cart);

        Integer quantity = -1;

        // When
        Item finalItem = item;
        Cart finalCart = cart;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.updateItemQuantity(finalCart.getId(),finalItem.getId(), quantity));
    }

    @DisplayName("Test Remove Item from Cart")
    @Test
    void testRemoveItem() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Product product = Product.builder()
                .id(null)
                .name("Product 1")
                .price(100.0)
                .availableQuantity(10)
                .category(Category.ELECTRONICS)
                .build();
        product = productRepository.save(product);

        Item item = Item.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .product(product)
                .category(product.getCategory())
                .build();
        item = itemRepository.save(item);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .totalCost(200.0)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item);
        cart = cartRepository.save(cart);

        // When
        Cart updatedCart = cartService.removeItem(cart.getId(), item.getId());

        // Then
        Assertions.assertNotNull(updatedCart);
        Assertions.assertEquals(0, updatedCart.getItems().size());
        Assertions.assertEquals(0.0, updatedCart.getTotalCost());
        Assertions.assertEquals(0.0, updatedCart.getBillDiscount());
    }

    @DisplayName("Test Remove Item from Cart with invalid cartId")
    @Test
    void testRemoveItemWithInvalidCart() {
        // Given
        Integer cartId = 100;
        Integer itemId = 1;

        // When
        Assertions.assertThrows(RuntimeException.class, () -> cartService.removeItem(cartId, itemId));
    }

    @DisplayName("Test Remove Item from Cart with invalid itemId")
    @Test
    void testRemoveItemWithInvalidItem() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        Integer itemId = 1;

        // When
        Cart finalCart = cart;
        Assertions.assertThrows(RuntimeException.class, () -> cartService.removeItem(finalCart.getId(), itemId));
    }

    @DisplayName("Test Get Cart with valid cartId")
    @Test
    void testGetCart() {
        // Given
        RetailUser retailUser = RetailUser.builder().id(null).name("John").email("test@tmail.com")
                .isAffiliated(false).isAffiliated(false).registeredOn(new Date()).build();
        retailUser = retailUserRepository.save(retailUser);

        Cart cart = Cart.builder()
                .id(null)
                .retailUser(retailUser)
                .items(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        // When
        Cart fetchedCart = cartService.getCart(cart.getId());

        // Then
        Assertions.assertNotNull(fetchedCart);
        Assertions.assertEquals(cart.getId(), fetchedCart.getId());
    }

    @DisplayName("Test Get Cart with invalid cartId")
    @Test
    void testGetCartWithInvalidCart() {
        // Given
        Integer cartId = 100;

        // When
        Assertions.assertThrows(RuntimeException.class, () -> cartService.getCart(cartId));
    }
}