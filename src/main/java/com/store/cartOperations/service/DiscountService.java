package com.store.cartOperations.service;

import com.store.cartOperations.domain.Cart;
import com.store.cartOperations.domain.Item;
import com.store.cartOperations.domain.RetailUser;
import com.store.cartOperations.repository.CartRepository;
import com.store.cartOperations.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

    @Value("${discount.bill}")
    private Double billDiscount;
    @Value("${discount.bill.rate}")
    private Double billDiscountRate;
    @Value("${category.noDiscount}")
    //@Value("#{'${category.noDiscount}'.split(',')}")
    private List<String> nonDiscountedItemCategories;
    @Value("${discount.user.employee}")
    private Double employeeDiscount;
    @Value("${discount.user.affiliated}")
    private Double affiliatedDiscount;
    @Value("${discount.loyalty}")
    private Double loyaltyDiscount;
    @Value("${discount.loyalty.period}")
    private Integer loyaltyPeriod;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;

    /**
     * This method applies various discounts to the given cart.
     * It first checks if the total cost of the cart is less than 0, and if so, throws a RuntimeException.
     * It then calculates the billing discount based on the total cost of the cart and the bill discount rate, and sets this value in the cart.
     * It also calculates the user discount by summing up the discounts of all items in the cart, and sets this value in the cart.
     * Finally, it saves the updated cart in the repository and returns it.
     *
     * @param cart The cart to which discounts are to be applied.
     * @return The updated cart with the applied discounts.
     * @throws RuntimeException if the total cost of the cart is less than 0.
     */

    @Transactional(Transactional.TxType.MANDATORY)
    public Cart apply(Cart cart) {
        Long start = System.currentTimeMillis();
        log.info("Applying discounts to cart for cartId - " + cart.getId());
        if (cart.getTotalCost() < 0) {
            log.error("Total cost is missing in the cart with id - " + cart.getId());
            throw new RuntimeException("Total cost is missing");
        }
        long billingDiscountTimes = (long) (cart.getTotalCost() / billDiscountRate);
        cart.setBillDiscount(billingDiscountTimes * billDiscount);
        Double userDiscount = cart.getItems().stream().map(x -> x.getDiscount()!=null?x.getDiscount():0.0).reduce(0.0, Double::sum);
        log.debug("User discount applied to cart with id - " + cart.getId() + " is - " + userDiscount);
        cart.setUserDiscount(userDiscount);
        cart = cartRepository.save(cart);
        log.info("Discounts applied to cart with id - " + cart.getId() + " in " + (System.currentTimeMillis() - start) + " ms");
        return cart;
    }

    // Generate documentation for the apply method

    /**
     * This method applies various discounts to the given item.
     * It first checks if the item is attached to a cart, and if not, throws a RuntimeException.
     * It then checks if the user is attached to the cart, and if not, throws a RuntimeException.
     * It then calculates the discount based on the user type and the item category, and sets this value in the item.
     * Finally, it saves the updated item in the repository, calls apply method to calculate discounts at cart and returns the updated cart.
     *
     * @param item The item to which discounts are to be applied.
     * @return The updated cart with the applied discounts.
     * @throws RuntimeException if the item is not attached to a cart or if the user is not attached to the cart.
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public Cart apply(Item item) {
        long start = System.currentTimeMillis();
        log.info("Applying discounts to item for itemId - " + item.getId());
        Double discount = 0.0;
        Cart cart = item.getCart();
        if (cart == null) {
            log.error("Item is not attached with cart for item id - " + item.getId());
            throw new RuntimeException("Item is not attached to cart");
        }
        RetailUser user = cart.getRetailUser();

        if (user == null) {
            log.error("User is not attached to cart with id - " + cart.getId());
            throw new RuntimeException("User is not attached to cart");
        }
        if (!nonDiscountedItemCategories.contains(item.getCategory().name())) {
            log.debug("Item with id - " + item.getId() + " is not in non discounted category");
            Date loyaltyPeriodStartBefore = Date.from(LocalDate.now().minusYears(loyaltyPeriod).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Double itemTotalCost = item.getPrice() * item.getQuantity();
            if (user.isEmployee()) {
                discount += (itemTotalCost * employeeDiscount / 100);
            } else if (user.isAffiliated()) {
                discount += (itemTotalCost * affiliatedDiscount / 100);
            } else if (user.getRegisteredOn().before(loyaltyPeriodStartBefore)) {
                discount += (itemTotalCost * loyaltyDiscount / 100);
            }
            item.setDiscount(discount);
            log.debug("Discount applied to item with id - " + item.getId() + " is - " + discount);
            item = itemRepository.save(item);
            Item finalItem = item;
            Double finalDiscount = discount;
            cart.getItems().stream().filter(x -> x.getId().equals(finalItem.getId())).findFirst().ifPresent(x -> x.setDiscount(finalDiscount));
        }

        cart = apply(cart);
        log.info("Discounts applied to item with id - " + item.getId() + " in " + (System.currentTimeMillis() - start) + " ms");
        return cart;
    }
}
