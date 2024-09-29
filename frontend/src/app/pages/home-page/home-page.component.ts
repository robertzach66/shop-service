import { AsyncPipe, JsonPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { OrderService } from '../../services/order/order.service';
import { ProductService } from '../../services/product/product.service';
import { Product } from '../../model/product';
import { Router } from '@angular/router';
import { Order } from '../../model/order';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [AsyncPipe, JsonPipe, FormsModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);

  isAuthenticated = false;
  orderSucces = false;
  orderFailed = false;
  quantityIsNull = false;

  products: Array<Product> = [];

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({isAuthenticated}) => {
        this.isAuthenticated = isAuthenticated;
        this.productService
          .getProducts()
          .pipe()
          .subscribe(products => {
            this.products = products;
          })
      }
    )
  }

  goToCreateProductPage() {
    this.router.navigateByUrl('/add-product');
  }

  orderProduct(product: Product, quantity: string) {

    this.oidcSecurityService.userData$.subscribe( result => {
      console.log(result);
      const userDetails = {
        email: result.userData.email,
        firstName: result.userData.given_name,
        lastName: result.userData.family_name,
      }

      if (!quantity) {
        this.orderFailed = true;
        this.orderSucces = false;
        this.quantityIsNull = true;
      } else {
        const order: Order = {
          skuCode: product.skuCode,
          price: product.price,
          quantity: Number(quantity),
          userDetails: userDetails,
        }

        this.orderService.orderProduct(order).subscribe({
            next: () => {this.orderSucces = true;}, 
            error: (e) => {this.orderFailed = true;}
          });
      }
    });
  }
}
