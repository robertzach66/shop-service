export interface Order {
    id?: string;
    orderNumber?: string;
    orderDate?: string;
    orderItems: Array<OrderItem>;
    userDetails: UserDetails;
}

export interface OrderItem {
    id?: string
    skuCode: string;
    price: number;
    quantity: number;
}

export interface UserDetails {
    email: string;
    firstName: string;
    lastName: string;
}