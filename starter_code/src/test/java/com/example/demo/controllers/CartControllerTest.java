package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private final static Logger logger = LoggerFactory.getLogger(CartControllerTest.class);
    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private Cart testCart;
    private Item testItem;


    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController,"userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        testItem = new Item();
        testItem.setId(0L);
        testItem.setName("UniqueItem");
        testItem.setPrice(new BigDecimal("3.99"));
        testItem.setDescription("1_AnItemOnlyForTestPurposes");
        testCart = new Cart();
        testCart.addItem(testItem);
    }

    @Test
    public void addToCart_nullUser() throws Exception{
        logger.debug("Testing: addToCart_nullUser...");
        ModifyCartRequest newRequest = new ModifyCartRequest();
        newRequest.setUsername(null);
        ResponseEntity<Cart> response = cartController.addTocart(newRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCart_happy_path() throws Exception{
        logger.debug("Testing: addToCart_happy_path...");
        ModifyCartRequest newRequest = new ModifyCartRequest();
        newRequest.setUsername("testUser");
        newRequest.setItemId(0L);
        newRequest.setQuantity(10);
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("P@ssw0rd");
        testUser.setCart(testCart);
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        //when(itemRepository.findById(newRequest.getItemId()).thenReturn(Optional.of(Item.class));
        ResponseEntity<Cart> response = cartController.addTocart(newRequest);
        assertNotNull(response);
    }


    @Test
    public void removeFromCart_happy_path() throws Exception{
        logger.debug("Testing: removeFromCart_happy_path...");
        ModifyCartRequest newRequest = new ModifyCartRequest();
        newRequest.setUsername("testUser");
        newRequest.setItemId(0L);
        newRequest.setQuantity(4);
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("P@ssw0rd");
        testUser.setCart(testCart);
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        ResponseEntity<Cart> response = cartController.removeFromcart(newRequest);
        assertNotNull(response);

    }
}
