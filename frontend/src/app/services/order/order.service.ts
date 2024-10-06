import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Order } from '../../model/order';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor(private httpClient: HttpClient) { }

  orderProduct(order: Order): Observable<Order> {
    return this.httpClient.post<Order>("http://localhost:9000/api/order", order);
  }

  getOrdersByEmail(email: string): Observable<Array<Order>> {
    const params = new HttpParams().set('email', email);
    return this.httpClient.get<Array<Order>>("http://localhost:9000/api/order", { params });
  }
}
