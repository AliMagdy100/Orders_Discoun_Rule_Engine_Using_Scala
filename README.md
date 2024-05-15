# Orders_Discoun_Rule_Engine_Using_Scala

## Overview

This project involves creating a rule engine for a large retail store to determine and apply discounts to order transactions based on a set of predefined rules. The engine qualifies transactions for discounts using various criteria and then calculates the appropriate discount amount. The final discounted price is computed and stored in a database. The project also involves logging engine events to a log file.

### Qualifying and Calculation Business Rules

- **Expiration Discount**: Products expiring in less than 30 days receive a discount.
- **Category Discount**: Cheese products receive a 10% discount; wine products receive a 5% discount.
- **Special Date Discount**: Orders made on March 23rd receive a 50% discount.
- **Quantity Discount**: Orders of more than 5 units receive a discount based on the quantity.
- **Channel Discount**: Orders made through the app receive a discount based on the quantity rounded to the nearest 5.
- **Payment Method Discount**: Orders paid using Visa receive a 5% discount.

### Solution Architecture

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



Handle exceptions, log errors, and close resources properly.

## Database Connection Management

Create a singleton object `ConnectionManager` to manage database connections.
