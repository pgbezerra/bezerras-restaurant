package com.pgbezerra.bezerras.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrderItemRepositoryTest {

	private static final List<Category> categories = new ArrayList<>();
	private static final List<Product> products = new ArrayList<>();
	private static final List<Order> orders = new ArrayList<>();
	private static final List<OrderItem> orderItems = new ArrayList<>();
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	
	
	{
		Category c1 = new Category(null, "Food");
		Category c2 = new Category(null, "Drink");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Product p2 = new Product(null, "Beer", BigDecimal.valueOf(25.0), c2);
		Product p3 = new Product(null, "Baiao de 2", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		Order o3 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.DESK, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		OrderItem oi2 = new OrderItem(null, p2, o1, Byte.valueOf("2"), BigDecimal.valueOf(30.0));
		orders.addAll(Arrays.asList(o1, o3));
		categories.addAll(Arrays.asList(c1, c2));
		products.addAll(Arrays.asList(p1, p2, p3));
		orderItems.addAll(Arrays.asList(oi1, oi2));
	}
	
	@Test
	public void inserOrderitemExpectedSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserOrderitemExpectedError() {
		Category c1 = new Category(null, "Food");
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, null, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
	}
	
	@Test
	public void findAllOrderItemExpectedSuccess() {
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		orderRepository.insertAll(orders);
		orderItemRepository.insertAll(OrderItemRepositoryTest.orderItems);
		List<OrderItem> orderItems = orderItemRepository.findAll();
		Assert.assertTrue(orderItems.size() > 0);
	}
	
	@Test
	public void findAllOrderExpectedError() {
		List<OrderItem> orderItems = orderItemRepository.findAll();
		Assert.assertFalse(orderItems.size() > 0);
	}
	
	@Test
	public void findOrderItemByIdExpectSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		Assert.assertTrue(orderItemRepository.findById(oi1.getId()).isPresent());
	}
	
	@Test
	public void findOrderItemByIdExpectedError() {
		Assert.assertTrue(orderItemRepository.findById(1L).isEmpty());
	}
	
	@Test
	public void updateOrderItemExpectSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		OrderItem orderItem = orderItemRepository.findById(oi1.getId()).get();
		oi1.setValue(BigDecimal.valueOf(30d));
		Assert.assertTrue(orderItemRepository.update(oi1));;
		oi1 = orderItemRepository.findById(oi1.getId()).get();
		Assert.assertTrue(!orderItem.getValue().equals(oi1.getValue()));
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderItemExpectError() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		oi1.setProduct(null);
		orderItemRepository.update(oi1);
	}
	
	@Test
	public void deleteItemByIdExpectedSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		Assert.assertTrue(orderItemRepository.deleteById(oi1.getId()));
	}
	
	@Test
	public void deleteItemByIdExpectedError() {
		Assert.assertFalse(orderItemRepository.deleteById(999L));
	}
	

}
