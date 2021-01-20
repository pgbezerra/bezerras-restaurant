package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.entities.dto.ReportDTO;
import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrderRepositoryTest {

	private List<OrderAddress> orderAddresses = new ArrayList<>();
	private List<Order> orders = new ArrayList<>();
	private List<Table> tables = new ArrayList<>();
	
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderAddressRepository orderAddressRepository;
	@Autowired
	private TableRepository tableRepository;
	
	
	{
		Table t1 = new Table(1, "Table 1");
		Table t2 = new Table(2, "Table 2");
		tables.addAll(Arrays.asList(t1, t2));
		OrderAddress oa1 = new OrderAddress(null,"Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		OrderAddress oa2 = new OrderAddress(null, "Client B", "Rua b", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddresses.addAll(Arrays.asList(oa1, oa2));
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, t1, OrderStatus.DOING, OrderType.TABLE, null);
		Order o2 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, null, OrderStatus.DOING, OrderType.DELIVERY, oa1);
		Order o3 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, t1, OrderStatus.DOING, OrderType.DESK, null);
		orders.addAll(Arrays.asList(o1, o2, o3));
	}
	
	@Test
	public void insertOrderWithouOrderAddressExpectedSuccess() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		orderRepository.insert(order);
		Assert.assertTrue(order.getId() > 0L);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserOrderWithoutOrderStatsExpectedError() {
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), null, OrderType.TABLE, null);
		orderRepository.insert(order);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserOrderWithInexistentOrderAddressExpetecError() {
		OrderAddress orderAddress = new OrderAddress(1L, "Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, null, OrderStatus.DOING, OrderType.DELIVERY, orderAddress);
		orderRepository.insert(order);
	}
	
	@Test
	public void insertOrderExpectedSuccess() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		OrderAddress orderAddress = new OrderAddress(null,"Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddressRepository.insert(orderAddress);
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, orderAddress);
		orderRepository.insert(order);
		Assert.assertTrue(order.getId() > 0L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateOrderWithInvalidORderTypeExpectedError() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		OrderAddress orderAddress = new OrderAddress(null,"Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddressRepository.insert(orderAddress);
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.DELIVERY, orderAddress);
		orderRepository.insert(order);
		order.setOrderType(999);
		orderRepository.update(order);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderWithoutValueExpectedError() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		OrderAddress orderAddress = new OrderAddress(null,"Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddressRepository.insert(orderAddress);
		Order order = new Order(null, new Date(), null, BigDecimal.ZERO, null, OrderStatus.DOING, OrderType.DELIVERY, orderAddress);
		orderRepository.insert(order);
		order.setValue(null);
		orderRepository.update(order);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderWithoutOrderTypeExpectedError() {
		Order order = new Order(null, new Date(), null, BigDecimal.ZERO, null, OrderStatus.DOING, null, null);
		orderRepository.insert(order);
		order.setOrderAddress(null);
		orderRepository.update(order);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderWithoutAllValuesExpectedError() {
		Order order = new Order(null, null, null, null, null, null, null, null);
		orderRepository.insert(order);
		order.setDate(null);
		order.setValue(null);
		order.setOrderAddress(null);
		order.setOrderStatus(null);
		order.setDeliveryValue(null);
		order.setOrderType(null);
		orderRepository.update(order);
	}
	
	@Test
	public void updateOrderExpectedSuccess() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		Order order = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		orderRepository.insert(order);
		order.setOrderStatus(1);
		Assert.assertTrue(orderRepository.update(order));
	}
	
	@Test
	public void updateInexistentOrderExpectedNoUpdates() {
		Order order = new Order(999L, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		order.setValue(BigDecimal.valueOf(500d));
		Assert.assertFalse(orderRepository.update(order));
	}
	
	@Test
	public void deleteOrderExpectedNoSuccess() {
		Assert.assertFalse(orderRepository.deleteById(999L));
	}
	
	@Test
	public void deleteOrderExpectedSuccess() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		Order order = new Order(999L, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, null, OrderStatus.DOING, OrderType.TABLE, null);
		orderRepository.insert(order);
		Assert.assertTrue(orderRepository.deleteById(order.getId()));
	}
	
	@Test
	public void findAllExpectedNoReturn() {
		List<Order> orders = orderRepository.findAll();
		Assert.assertEquals(0, orders.size());
	}
	
	@Test
	public void findAllExpectReturn() {
		tableRepository.insertAll(tables);
		orderAddressRepository.insertAll(orderAddresses);
		orderRepository.insertAll(orders);
		List<Order> orders = orderRepository.findAll();
		Assert.assertNotEquals(0, orders.size());
	}

	@Test
	public void findPendingExpectReturn() {
		tableRepository.insertAll(tables);
		orderAddressRepository.insertAll(orderAddresses);
		orderRepository.insertAll(orders);
		List<Order> orders = orderRepository.findAll();
		Assert.assertNotEquals(0, orders.size());
	}
	
	@Test
	public void findByIdNonexistentOrderExpectedError() {
		Optional<Order> order = orderRepository.findById(999L);
		Assert.assertFalse(order.isPresent());
	}
	
	@Test
	public void findByIdExpectedSuccess() {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		Order order = new Order(999L, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		orderRepository.insert(order);
		List<Order> orderReturn = orderRepository.findPendingOrders();
		Assert.assertFalse(orderReturn.isEmpty());
	}

	@Test
	public void findOrderReportExpectedSuccess() throws ParseException {
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		Date initialDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
		Date finalDate = new Date();
		Order o1 = new Order(999L, initialDate, BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.COMPLETE, OrderType.TABLE, null);
		Order o2 = new Order(999L, finalDate, BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.COMPLETE, OrderType.TABLE, null);
		orderRepository.insertAll(Arrays.asList(o1, o2));
		List<ReportDTO> listReport = orderRepository.report(initialDate, finalDate);
		Assert.assertFalse(listReport.isEmpty());
	}

	@Test
	public void findOrderReportExpectedError() throws ParseException {
		Date initialDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
		Date finalDate = new Date();
		List<ReportDTO> listReport = orderRepository.report(initialDate, finalDate);
		Assert.assertTrue(listReport.isEmpty());
	}

}
