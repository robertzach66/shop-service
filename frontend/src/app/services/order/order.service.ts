import { HttpClient, HttpHeaders } from '@angular/common/http';
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
}
