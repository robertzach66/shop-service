export interface Order {
    id?: string;
    orderNumber?: string;
    orderDate?: string;
    orderItems: Array<OrderItem>;
    customer: Customer;
}

export interface OrderItem {
    id?: string
    skuCode: string;
    price: number;
    quantity: number;
}

export interface Customer {
    email: string;
    firstName: string;
    lastName: string;
}