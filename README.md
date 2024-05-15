# Orders_Discount_Rule_Engine_Using_Scala

## Overview

This project involves creating a rule engine for a large retail store to determine and apply discounts to order transactions based on a set of predefined rules. The engine qualifies transactions for discounts using various criteria and then calculates the appropriate discount amount. The final discounted price is computed and stored in a database. The project also involves logging engine events to a log file.

## Qualifying and Calculation Business Rules

- **Expiration Discount**: Products expiring in less than 30 days receive a discount.
- **Category Discount**: Cheese products receive a 10% discount; wine products receive a 5% discount.
- **Special Date Discount**: Orders made on March 23rd receive a 50% discount.
- **Quantity Discount**: Orders of more than 5 units receive a discount based on the quantity.
- **Channel Discount**: Orders made through the app receive a discount based on the quantity rounded to the nearest 5.
- **Payment Method Discount**: Orders paid using Visa receive a 5% discount.

## Solution Architecture

![siu](https://github.com/AliMagdy100/Orders_Discount_Rule_Engine_Using_Scala/assets/87953057/3e5e7991-0e25-4eb6-86a0-b4e61ef8d7b7)
1. **Input Orders**:
   - Multiple "Order" inputs are fed into the system. These represent incoming orders or data points.

2. **Qualification Process**:
   - Each "Order" is processed by a "Qualifier" function (`Func<Order,bool>`).
   - The qualifier determines whether an order meets specific criteria.
   - If an order passes the qualification, it proceeds to the next step.

3. **Calculations**:
   - Qualified orders are sent to "Calculator" functions (`Func<Order,Double>`).
   - These calculators perform numerical operations on the order data to compute the discount.

4. **Aggregation and Analysis**:
   - The system performs additional processes labeled as “OrderBy”, “Take”, and “Avg”:
     - **OrderBy**: discount are sorted decsendingly.
     - **Take**: We Take the top 2 discount.
     - **Avg**: calculate the discount based on the average from this two values.
    
## Solution Steps
    
1. **Imports**: Import necessary Scala libraries for file operations, date and time manipulation, and database connectivity.
2. **Logging Setup**:Create a log file and define a method `logEvent` to write log messages with timestamps.
3. **Start Logging**:Log the start of the application.
4. **Read Input Data**: Read order details from a CSV file and parse it into a list of strings.
5. **Define Case Classes**:Define case classes to represent orders with different attributes, including discounts and final prices.
6. **Parse CSV Data**:Define a function `toOrder` to convert a CSV line into an `Order` object.
7. **Discount Rules**: Define qualification and calculation functions to determine if an order qualifies for a discount and calculate the discount amount.
8. **Define Discount Rules List**:Create a list of discount rules, pairing qualification and calculation functions.
9. **Apply Discount Rules**:Define a function `getOrderWithDiscount` to apply discount rules to an order and create an `OrderWithDiscount` object.
10. **Calculate Final Price**: Define a function `getOrderWithFinalPrice` to calculate the final price after applying discounts.
11. **Database Operations**:Define a function `insertOrder` to insert an order into a database.
12. **Process Orders**:Convert lines to orders, apply discounts, calculate final prices, and insert each order into the database.
13. **Database Connection Management**: Create a singleton object `ConnectionManager` to manage database connections.

##Scala Optmization Techniques
1. **Defining Case Classes**:
instead of parsing the order string in each function, I utilized case classes for easiness of field accessing and immutability property
![image](https://github.com/AliMagdy100/Orders_Discount_Rule_Engine_Using_Scala/assets/87953057/8210ecb6-e813-48c7-b361-0e0f6dbb44ed)
2. **Singleton Object for Database Connection**:
instead of creating a DB connection for each funcction call, I utilized the concept of singleton which allow only one connection object to be created
![image](https://github.com/AliMagdy100/Orders_Discount_Rule_Engine_Using_Scala/assets/87953057/67ad85fe-a033-4085-b192-439290ad5c30)
3. **Lazily Evaluated DB connection**:
instead of initiated a connection once the program start, I utilized the concept of lazy evaluation for only creating the connection when needed(called).
![image](https://github.com/AliMagdy100/Orders_Discount_Rule_Engine_Using_Scala/assets/87953057/54bb41e6-78e3-4960-b3e4-68724550a2dc)




