package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private final static Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    private OrderController orderController;
    private User testUser;
    private Cart testCart;
    private Item testItem1;
    private Item testItem2;
    private UserOrder testOrder;

    // mocking other components
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);

        // create test data
        createUserAndPutItemsInCart();
        placeOrderForUser(testUser);
    }

    @Test
    public void getOrderHistoryForUser_nullUser() throws Exception{
        logger.debug("Testing: getOrderHistoryForNullUser...");
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(null);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrderHistoryForUser() throws Exception{
        logger.debug("Testing: getOrderHistoryForUser...");
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(testUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, testCart.getItems().size());
    }


    @Test
    public void submitOrderForUser(){
        logger.debug("Testing: submitOrderForUser...");
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        final ResponseEntity<UserOrder> response = orderController.submit(testUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new BigDecimal("9.98"), testCart.getTotal());
    }

    private void createUserAndPutItemsInCart(){
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("P@ssw0rd");
        testItem1 = new Item();
        testItem1.setId(0L);
        testItem1.setName("UniqueItem");
        testItem1.setPrice(new BigDecimal("3.99"));
        testItem1.setDescription("1_AnItemOnlyForTestPurposes");
        testItem2 = new Item();
        testItem2.setId(1L);
        testItem2.setName("VeryUniqueItem");
        testItem2.setPrice(new BigDecimal("5.99"));
        testItem2.setDescription("2_AnItemOnlyForTestPurposes");
        testCart = new Cart();
        testCart.addItem(testItem1);
        testCart.addItem(testItem2);
        TestUtils.injectObject(testUser, "cart", testCart);
    }

    private void placeOrderForUser(User testUser){
        testOrder = new UserOrder();
        testOrder.setUser(testUser);
        testOrder.setId(0L);
        testOrder.setItems(testUser.getCart().getItems());
        testOrder.setTotal(new BigDecimal("9.98"));
    }
}
