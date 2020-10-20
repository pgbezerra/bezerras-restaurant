package com.pgbezerra.bezerras.entities.enums;

public enum OrderStatus {

	COMPLETE(1), DOING(2), CANCELED(3);

	private Integer statusCode;

	public Integer getStatusCode() {
		return statusCode;
	}

	private OrderStatus(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public static OrderStatus getByStatusCode(Integer statusCode) {
		if (statusCode <= 0)
			throw new IllegalArgumentException(String.format("Invalid status code[%s], status must be greater than 0", statusCode));
		for(OrderStatus status: OrderStatus.values())
			if(status.getStatusCode().equals(statusCode))
				return status;
		throw new IllegalArgumentException(String.format("Invalid status code[%s], status not found", statusCode));
	}

}
