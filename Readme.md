# E-Commerce Website for Product Selling

1. Project Overview

The main objective of this project is to build a complete E-commerce Website where a customer can browse different products online, add preferred products to the cart, and place orders.

The customer will be able to view previous and current orders from their account and check the current status of each order.

The website will have a separate Admin Dashboard. From the Admin Dashboard, the administrator will be able to manage products, customers, and orders.

---

2. User Roles

There will primarily be two types of users in the system.

2.1 Customer

Customer website-এ এসে:

* Account তৈরি will be able to
* Login will be able to
* Product view/browse পারবে
* Product search will be able to
* Product details view/browse পারবে
* Product Cart-এ add will be able to
* Cart থেকে product remove will be able to
* Product quantity পরিবর্তন will be able to
* Checkout will be able to
* Order place will be able to
* নিজের all order view/browse পারবে
* a specific order's details view/browse পারবে
* Order status view/browse পারবে
* Delivery tracking view/browse পারবে

2.2 Admin

From the Admin Dashboard, the administrator will be able to:

* Dashboard statistics view/browse পারবে
* Product add will be able to
* Product edit will be able to
* Product delete will be able to
* Product stock manage will be able to
* all customer view/browse পারবে
* all order view/browse পারবে
* কোন customer কোন product order করেছে view/browse পারবে
* Order status update will be able to
* Payment status view/browse পারবে
* Delivery details manage will be able to

---

3. Customer Side Pages

3.1 Home Page

The Home Page will contain:

* Website logo
* Navigation menu
* Search bar
* Login/Register button
* Cart icon
* Product categories
* Featured products
* Latest products
* Offer banner
* Footer

Navigation Example

Home | Shop | Categories | My Orders | Cart | Profile

---

4. Authentication Module

4.1 Customer Registration

A new customer will create an account using the following information:

* Full Name
* Email Address
* Mobile Number
* Password
* Confirm Password

After successful registration, the customer will be able to log in.

4.2 Customer Login

For customer login, the following can be used:

* Email অথবা Mobile Number
* Password

can be used.

4.3 Forgot Password

If the customer forgets their password:

* Email/Mobile দিয়ে account verify করবে
* OTP বা reset link পাবে
* new password সেট will be able to

---

5. Product Module

  5.1 Product List Page

This page will display all available products.

Each Product Card will contain:

*  Multiple Product Image
* Product Name
* Product Price
* Discount Price
* Available Stock
* Add to Cart Button
* View Details Button

Example:

Product Image

Wireless Headphone

₹999

₹1,299

20% OFF

[Add to Cart]

[View Product]

---

5.2 Product Details Page

When the customer clicks on a specific product, they will be able to view the Product Details Page.

It will contain:

* Multiple Product Images
* Product Name
* Product Description
* Product Price
* Discount
* Available Stock
* Quantity Selector
* Add to Cart Button
* Buy Now Button

Example:

Product: Wireless Headphone

Price: ₹999

Stock Available: 25

Quantity:

[-] 1 [+]

[Add to Cart]

[Buy Now]

---

6. Shopping Cart Module

When the customer adds a product to the cart, that product will be added to the Cart Page.

Cart-এ will contain:

* Product Image
* Product Name
* Product Price
* Quantity
* Subtotal
* Remove Button

The customer will be able to:

* Quantity increase পারবে
* Quantity decrease পারবে
* Product remove will be able to

Example:

Cart Items

1. Wireless Headphone

Price: ₹999

Quantity: [-] 2 [+]

Subtotal: ₹1,998

[Remove]

---

Order Summary:

Product Total: ₹1,998

Delivery Charge: ₹50

Discount: ₹100

---

Total Amount: ₹1,948

[Proceed to Checkout]

---

7. Checkout Module

On the Checkout Page, the customer will provide their delivery information.

Required Information

* Full Name
* Mobile Number
* Email Address
* House/Flat Address
* Area
* City
* State
* PIN Code
* Landmark

The customer will then select a payment method.

Payment Options

* Online Payment
* Cash on Delivery (COD)

The order summary will be displayed.

Example:

Products Total: ₹1,998

Shipping Charge: ₹50

Discount: ₹100

---

Grand Total: ₹1,948

Payment Method:

○ Online Payment

○ Cash on Delivery

[Place Order]

---

8. Order Management for Customer

After the order is successfully placed, a unique Order ID will be generated.

Example:

Order ID: ORD-2026-00001

The customer will be able to view all orders from their account.

My Orders Page

The following will be displayed for each order:

* Order ID
* Product Image
* Product Name
* Quantity
* Total Amount
* Order Date
* Payment Status
* Order Status

Example:

Order ID: ORD-2026-00001

Product: Wireless Headphone

Quantity: 1

Amount: ₹999

Payment: Paid

Order Status: Shipped

[View Details]

---

9. Order Status Flow

The order will have different statuses:

1. Order Placed
2. Order Confirmed
3. Processing
4. Packed
5. Shipped
6. In Transit
7. Out for Delivery
8. Delivered

Additional statuses:

* Cancelled
* Returned
* Failed Delivery

Order Flow

Customer Order

↓

Order Placed

↓

Admin Confirmed

↓

Processing

↓

Packed

↓

Shipped

↓

In Transit

↓

Out for Delivery

↓

Delivered

---

10. Order Tracking

The customer will be able to select a specific order and view its tracking information.

Example:

Order #ORD-2026-00001

✓ Order Placed

✓ Order Confirmed

✓ Packed

✓ Shipped

● In Transit

○ Out for Delivery

○ Delivered

Current Status:

Your product is currently in transit.

Expected Delivery:

25 August 2026

After Shiprocket integration, the customer's order tracking can be displayed in greater detail using tracking information from the shipping provider.

---

11. Customer Profile

The customer will be able to view and update the following from the Profile Page:

* Name
* Email
* Mobile Number
* Profile Image
* Default Address
* Multiple Delivery Addresses

The Customer Dashboard will contain:

My Profile

My Orders

My Addresses

My Cart

Logout

---

12. Admin Dashboard

A completely separate Dashboard will be provided for the Admin.

## Dashboard Overview

The Dashboard will contain summary cards:

* Total Customers
* Total Products
* Total Orders
* Pending Orders
* Processing Orders
* Shipped Orders
* Delivered Orders
* Total Sales

Example:

Total Users: 250

Total Products: 120

Total Orders: 580

Pending Orders: 15

Total Revenue: ₹4,50,000

---
13. Admin Product Management

From the Admin Product Management Page, the administrator will be able to:

* Add Product
* Edit Product
* Delete Product
* Activate/Deactivate Product
* Update Stock

will be able to do so.

## Add Product Form

The admin will provide the following information:

* Product Name
* Product Category
* Product Description
* Regular Price
* Selling Price
* Discount
* Stock Quantity
* Product SKU
* Product Images
* Product Status

Example:

Product Name: Wireless Headphone

Category: Electronics

Regular Price: ₹1,299

Selling Price: ₹999

Stock: 50

Status: Active

[Save Product]

---

14. Admin Order Management

This is one of the most important parts of the project.

The admin will be able to view all customer orders.

The Order List will contain:

| Order ID | Customer  | Product   | Amount | Payment | Status     |
| -------- | --------- | --------- | -----: | ------- | ---------- |
| ORD001   | Rahul Das | Headphone |   ₹999 | Paid    | Shipped    |
| ORD002   | Amit Roy  | T-Shirt   |   ₹599 | COD     | Processing |
| ORD003   | Suman Pal | Watch     | ₹1,299 | Paid    | Delivered  |

When the admin clicks on an order, they will be able to view the complete details.

---

15. Admin Order Details

The admin will be able to view:

Customer Information

* Customer Name
* Email
* Mobile Number

Delivery Address

* Full Address
* City
* State
* PIN Code

Ordered Products

* Product Name
* Product Image
* Quantity
* Price
* Total Price

Payment Information

* Payment Method
* Payment Status
* Transaction ID

Shipping Information

* Courier Partner
* Tracking ID
* Current Shipping Status

Admin এখান থেকে Order Status পরিবর্তন will be able to do so.

Example:

Current Status:

Processing

Change Status:

[Processing ▼]

Options:

* Confirmed
* Processing
* Packed
* Shipped
* Delivered
* Cancelled

[Update Order]

---

16. Admin Customer Management

The admin will be able to view all registered customers.

Customer List:

| Customer ID | Name      | Email                                     | Mobile     | Total Orders |
| ----------- | --------- | ----------------------------------------- | ---------- | -----------: |
| C001        | Rahul Das | [rahul@email.com](mailto:rahul@email.com) | 9876543210 |            5 |
| C002        | Amit Roy  | [amit@email.com](mailto:amit@email.com)   | 9876543211 |            2 |

When the admin selects a customer, they will be able to view:

* Customer Profile
* Customer Address
* Total Orders
* Order History
* Total Purchase Amount

---

17. Inventory Management

Product stock will be managed automatically.

Example:

Before Order:

Wireless Headphone

Stock: 50

Customer ordered: 2

After Order:

Stock: 48

If the product goes out of stock:

Out of Stock

will be displayed.

The customer will not be able to order that product.

---

18. Database Structure

The main database tables for the system will be:

## Users Table

* id
* name
* email
* mobile
* password
* role
* created_at
* updated_at

Role:

* CUSTOMER
* ADMIN

---

## Products Table

* id
* name
* description
* category_id
* sku
* regular_price
* selling_price
* stock
* status
* created_at
* updated_at

---

## Product Images Table

* id
* product_id
* image_url
* is_primary

---

## Categories Table

* id
* name
* description
* image
* status

---

## Cart Table

* id
* user_id
* created_at

---

## Cart Items Table

* id
* cart_id
* product_id
* quantity
* price

---

## Orders Table

* id
* order_number
* user_id
* total_amount
* shipping_charge
* discount
* payment_method
* payment_status
* order_status
* created_at
* updated_at

---

## Order Items Table

* id
* order_id
* product_id
* product_name
* product_price
* quantity
* total_price

When a product is ordered, its product information will be saved in the Order Items Table.

---

## Addresses Table

* id
* user_id
* full_name
* mobile
* address_line
* city
* state
* pin_code
* is_default

---

## Payments Table

* id
* order_id
* payment_method
* transaction_id
* amount
* payment_status
* created_at

---

## Shipments Table

* id
* order_id
* courier_name
* shipment_id
* tracking_id
* tracking_url
* shipment_status
* estimated_delivery

---

19. Complete System Flow

## Customer Side Flow

Customer

↓

Register/Login

↓

Browse Products

↓

View Product Details

↓

Add Product to Cart

↓

View Cart

↓

Update Quantity

↓

Proceed to Checkout

↓

Enter Delivery Address

↓

Select Payment Method

↓

Place Order

↓

Order Created

↓

View My Orders

↓

Track Order

↓

Product Delivered

---

# 20. Admin Side Flow

Admin Login

↓

Admin Dashboard

↓

View New Orders

↓

Check Customer Details

↓

Check Ordered Products

↓

Confirm Order

↓

Pack Product

↓

Create Shipment

↓

Update Order Status

↓

Customer Tracks Order

↓

Product Delivered

---

# 21. Recommended Technology Stack

## Frontend

* Vue.js 3
* TypeScript
* Pinia
* Vue Router
* Tailwind CSS
* Axios

## Backend

* Spring Boot
* Spring Security
* REST API
* JPA / Hibernate

## Database

* MySQL

## Image Storage

* Cloudinary or similar cloud storage

## Payment Gateway

* Razorpay or another suitable payment provider

## Delivery Integration

* Shiprocket

---

---

# 23. Security Requirements

System-এ must:

* Password encrypt করতে হবে
* Customer শুধুমাত্র নিজের order view/browse পারবে
* Admin route protected will contain
* Admin and Customer's role আলাদা হবে
* Unauthorized user admin dashboard access will be able to না
* Payment-related sensitive information secure রাখতে হবে
* Input validation করতে হবে

---

# 24. Future Features

The following features can be added later:

* Wishlist
* Product Review and Rating
* Coupon System
* Discount System
* Referral System
* Multiple Admin
* Vendor/Seller Panel
* Return and Refund
* Invoice PDF
* Email Notifications
* SMS Notifications
* WhatsApp Notifications
* Shiprocket Live Tracking
* Sales Analytics
* Low Stock Alert
* Product Recommendation System

---

# 25. Minimum Viable Product (MVP)

For the first version of the website, the following features will be sufficient:

### Customer

* Registration/Login
* Product List
* Product Details
* Add to Cart
* Cart Management
* Checkout
* Place Order
* My Orders
* Order Status

### Admin

* Admin Login
* Dashboard
* Product Management
* Customer List
* Order List
* Order Details
* Order Status Update

### Phase 2

* Online Payment
* COD
* Shiprocket Integration
* Live Tracking
* Coupon
* Wishlist
* Invoice

---

# Conclusion

এই E-commerce Website-এর মাধ্যমে customer সহজে product browse, cart-এ add and order will be able to do so. Customer তার নিজের order history and delivery status view/browse পারবে।

অন্যদিকে Admin Dashboard থেকে administrator complete business পরিচালনা will be able to do so. Admin জানতে পারবে:

* কে order করেছে
* কোন product order করেছে
* কত quantity order করেছে
* কত টাকা payment করেছে
* কোন address-এ delivery হবে
* Order currentে কোন অবস্থায় আছে

Later, Shiprocket integration can be used to add order shipping, courier booking, and product tracking capabilities.