package com.store.cartOperations.service;

import com.store.cartOperations.domain.*;
import com.store.cartOperations.repository.CartRepository;
import com.store.cartOperations.repository.ItemRepository;
import com.store.cartOperations.repository.ProductRepository;
import com.store.cartOperations.repository.RetailUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiscountServiceTest {

    @Autowired
    private DiscountService discountService;
    private List<Product> products;

    private List<RetailUser> users;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RetailUserRepository userRepository;

    @BeforeAll
    public void setUp() {
        Product product1 = Product.builder().id(1).name("Rice").price(100.0).availableQuantity(25).category(Category.GROCERY).build();
        Product product2 = Product.builder().id(2).name("Wheat").price(110.0).availableQuantity(50).category(Category.GROCERY).build();
        Product product3 = Product.builder().id(3).name("heater").price(150.0).availableQuantity(10).category(Category.ELECTRONICS).build();
        Product product4 = Product.builder().id(4).name("Ball").price(20.0).availableQuantity(100).category(Category.SPORTS).build();
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
        product3 = productRepository.save(product3);
        product4 = productRepository.save(product4);
        products = List.of(product1, product2, product3, product4);
        products = productRepository.saveAll(products);

        RetailUser retailUser1 = RetailUser.builder().id(null).name("Employee").email("john@tmail.com")
                .isEmployee(true).isAffiliated(false).registeredOn(new Date()).build();

        RetailUser retailUser2 = RetailUser.builder().id(null).name("affiliatedUser").email("krish@tmail.com")
                .isEmployee(false).isAffiliated(true).registeredOn(new Date()).build();

        RetailUser retailUser3 = RetailUser.builder().id(null).name("booby").email("booby@tmail.com")
                .isEmployee(true).isAffiliated(true).registeredOn(
                        Date.from(LocalDate.now().minusYears(2).minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();

        RetailUser retailUser4 = RetailUser.builder().id(null).name("newUser").email("shiv@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(
                        Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();

        RetailUser retailUser5 = RetailUser.builder().id(null).name("oldUser").email("shiv@tmail.com")
                .isEmployee(false).isAffiliated(false).registeredOn(
                        Date.from(LocalDate.now().minusYears(2).minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        users = List.of(retailUser1, retailUser2, retailUser3, retailUser4, retailUser5);
        users = userRepository.saveAll(users);
    }

    @BeforeEach
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void init() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }
    @AfterAll
    public void cleanUp() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Test apply discount method to Item with user as Employee and Category as GROCERY")
    @Test
    void testApplyDiscountToItemWithUserAsEmployeeCategoryAsGrocery() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();

        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(200.0)
                .retailUser(users.get(0))
                .items(List.of(item1))
                .build();

        Cart cart1 = cartRepository.save(cart);
        item1.setCart(cart1);
        //When
        Cart updateCart = discountService.apply(item1);

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(10.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
    }

    @DisplayName("Test apply discount method to Item with user as Employee and Category as not GROCERY")
    @Test
    void testApplyDiscountToItemWithUserAsEmployeeCategoryAsNotGrocery() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item3 = Item.builder().id(2).name("heater").price(150.0).quantity(5).category(Category.ELECTRONICS).product(products.get(2)).discount(0.0).build();
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(950.0)
                .retailUser(users.get(0))
                .items(new ArrayList<>())
                .build();

        item1.setCart(cart);
        item3.setCart(cart);
        cart.getItems().add(item1);
        cart.getItems().add(item3);
        Cart finalCart = cartRepository.save(cart);

        //When
        Cart updateCart = discountService.apply(finalCart.getItems().get(1));

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(45.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(225.0);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(1).getDiscount()).isEqualTo(225.0);
    }

    @DisplayName("Test apply discount method to Item with user as Affiliate")
    @Test
    void testApplyDiscountToItemWithUserAsAffiliated() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item2 = Item.builder().id(2).name("Wheat").price(110.0).quantity(10).category(Category.GROCERY).product(products.get(1)).discount(0.0).build();
        Item item3 = Item.builder().id(3).name("heater").price(150.0).quantity(5).category(Category.ELECTRONICS).product(products.get(2)).discount(0.0).build();
        Item item4 = Item.builder().id(4).name("Ball").price(20.0).quantity(50).category(Category.SPORTS).product(products.get(3)).discount(0.0).build();
        List<Item> items = List.of(item1, item2, item3, item4);
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(2750.0)
                .retailUser(users.get(1))
                .items(items)
                .build();
        items.forEach(item -> item.setCart(cart));
        Cart finalCart = cartRepository.save(cart);

        //When
        Cart updateCart = discountService.apply(finalCart.getItems().get(2));

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(135.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(75.0);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(2).getDiscount()).isEqualTo(75.0);
    }

    @DisplayName("Test apply discount method to Item with user as loyal customer")
    @Test
    void testApplyDiscountToItemWithUserAsLoyal() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item2 = Item.builder().id(2).name("Wheat").price(110.0).quantity(10).category(Category.GROCERY).product(products.get(1)).discount(0.0).build();
        Item item3 = Item.builder().id(3).name("heater").price(150.0).quantity(5).category(Category.ELECTRONICS).product(products.get(2)).discount(0.0).build();
        Item item4 = Item.builder().id(4).name("Ball").price(20.0).quantity(50).category(Category.SPORTS).product(products.get(3)).discount(0.0).build();
        List<Item> items = List.of(item1, item2, item3, item4);
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(2750.0)
                .retailUser(users.get(4))
                .items(items)
                .build();

        items.forEach(item -> item.setCart(cart));
        Cart finalCart = cartRepository.save(cart);
        //When
        Cart updateCart = discountService.apply(finalCart.getItems().get(2));

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(135.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(37.5);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(2).getDiscount()).isEqualTo(37.5);
    }

    @DisplayName("Test apply discount method to Item with user recently registered")
    @Test
    void testApplyDiscountToItemWithUserRecentlyRegistered() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item2 = Item.builder().id(2).name("Wheat").price(110.0).quantity(10).category(Category.GROCERY).product(products.get(1)).discount(0.0).build();
        Item item3 = Item.builder().id(3).name("heater").price(150.0).quantity(5).category(Category.ELECTRONICS).product(products.get(2)).discount(0.0).build();
        Item item4 = Item.builder().id(4).name("Ball").price(20.0).quantity(50).category(Category.SPORTS).product(products.get(3)).discount(0.0).build();
        List<Item> items = List.of(item1, item2, item3, item4);
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(2750.0)
                .retailUser(users.get(3))
                .items(items)
                .build();
        Cart finalCart = cartRepository.save(cart);
        items.forEach(item -> item.setCart(finalCart));
        //When
        Cart updateCart = discountService.apply(items.get(2));

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(135.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(2).getDiscount()).isEqualTo(0.0);
    }

    @DisplayName("Test apply discount method to Item with user registered 2 years ago and is an employee and affiliated")
    @Test
    void testApplyDiscountToItemWithUserAsEmployeeAndAffiliated() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item2 = Item.builder().id(2).name("Wheat").price(110.0).quantity(10).category(Category.GROCERY).product(products.get(1)).discount(0.0).build();
        Item item3 = Item.builder().id(3).name("heater").price(150.0).quantity(5).category(Category.ELECTRONICS).product(products.get(2)).discount(0.0).build();
        Item item4 = Item.builder().id(4).name("Ball").price(20.0).quantity(50).category(Category.SPORTS).product(products.get(3)).discount(0.0).build();
        List<Item> items = List.of(item1, item2, item3, item4);
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(2750.0)
                .retailUser(users.get(2))
                .items(items)
                .build();
        items.forEach(item -> item.setCart(cart));
        Cart finalCart = cartRepository.save(cart);
        itemRepository.saveAll(items);
        //When
        Cart updateCart = discountService.apply(finalCart.getItems().get(2));

        //Then
        assertThat(updateCart).isNotNull();
        assertThat(updateCart.getBillDiscount()).isEqualTo(135.0);
        assertThat(updateCart.getUserDiscount()).isEqualTo(225.0);
        assertThat(updateCart.getItems().get(0).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(1).getDiscount()).isEqualTo(0.0);
        assertThat(updateCart.getItems().get(2).getDiscount()).isEqualTo(225.0);
    }

    @DisplayName("Test apply discount method to Item which is not present in the cart")
    @Test
    void testApplyDiscountToItemWhichIsNotPresentInTheCart() {
        //Given
        Item item1 = Item.builder().id(1).name("Rice").price(100.0).quantity(2).category(Category.GROCERY).product(products.get(0)).discount(0.0).build();
        Item item2 = Item.builder().id(2).name("Wheat").price(110.0).quantity(10).category(Category.GROCERY).product(products.get(1)).discount(0.0).build();
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(200.0)
                .retailUser(users.get(0))
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(item1);
        Cart cart1 = cartRepository.save(cart);
        item1.setCart(cart1);

        //When
        Assertions.assertThrows(RuntimeException.class, () -> discountService.apply(item2));
    }

    @DisplayName("Test apply discount method to cart where total cost is less than 0")
    @Test
    void testApplyDiscountToCartWithCostLessThanZero() {
        //Given
        Cart cart = Cart.builder()
                .id(1)
                .billDiscount(0.0)
                .userDiscount(0.0)
                .totalCost(-10.0)
                .retailUser(users.get(0))
                .items(new ArrayList<>())
                .build();
        cartRepository.save(cart);

        //When
        Assertions.assertThrows(RuntimeException.class, () -> discountService.apply(cart));
    }
}
