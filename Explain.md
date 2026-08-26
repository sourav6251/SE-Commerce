# E-Commerce System

```text
                    E-COMMERCE SYSTEM
                           │
             ┌─────────────┴─────────────┐
             │                           │
        CUSTOMER SIDE                ADMIN SIDE
             │                           │
     Browse Products                Manage Products
     Search Products                Manage Customers
     Product Details                Manage Orders
     Add to Cart                    Manage Stock
     Checkout                       Update Order Status
     Payment                        Manage Delivery
     My Orders                      View Sales
     Track Order                    Dashboard
```


# Hexagonal Architecture

## Adapter
**Adapter = how the application communicates with the outside world**

- `adapter/in` → things coming into the application
- `adapter/out` → things going out of the application

## Port
**Port = contract/interface between the application and adapters**

- `application/port/in` → interfaces for incoming use cases
- `application/port/out` → interfaces for external capabilities required by the application

## Application Service
**Application Service = coordinates a use case**

- Implements `port/in` interfaces
- Calls `port/out` interfaces
- Coordinates application workflow

## Domain
**Domain = core business logic**

- Business rules
- Entities
- Value Objects
- Domain Services
- Domain Exceptions
