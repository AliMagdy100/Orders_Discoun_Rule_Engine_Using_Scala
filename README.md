# Orders_Discoun_Rule_Engine_Using_Scala

## Overview

This project involves creating a rule engine for a large retail store to determine and apply discounts to order transactions based on a set of predefined rules. The engine qualifies transactions for discounts using various criteria and then calculates the appropriate discount amount. The final discounted price is computed and stored in a database. The project also involves logging engine events to a log file.

### Problem Statement

The rule engine must:

1. Qualify orders for discounts based on specific rules.
2. Calculate discounts using predefined calculation rules.
3. Apply the top two discounts (if applicable) and average them.
4. Store the results, including the final price, in a database.
5. Log events in a specified format.

### Qualifying and Calculation Rules

- **Expiration Discount**: Products expiring in less than 30 days receive a discount.
- **Category Discount**: Cheese products receive a 10% discount; wine products receive a 5% discount.
- **Special Date Discount**: Orders made on March 23rd receive a 50% discount.
- **Quantity Discount**: Orders of more than 5 units receive a discount based on the quantity.
- **Channel Discount**: Orders made through the app receive a discount based on the quantity.
- **Payment Method Discount**: Orders paid using Visa receive a 5% discount.

## Imports

Import necessary Scala libraries for file operations, date and time manipulation, and database connectivity.

## Logging Setup

Create a log file and define a method `logEvent` to write log messages with timestamps.

## Start Logging

Log the start of the application.

## Read Input Data

Read order details from a CSV file and parse it into a list of strings.

## Define Case Classes

Define case classes to represent orders with different attributes, including discounts and final prices.

## Parse CSV Data

Define a function `toOrder` to convert a CSV line into an `Order` object.

## Discount Rules

Define qualification and calculation functions to determine if an order qualifies for a discount and calculate the discount amount.

## Define Discount Rules List

Create a list of discount rules, pairing qualification and calculation functions.

## Apply Discount Rules

Define a function `getOrderWithDiscount` to apply discount rules to an order and create an `OrderWithDiscount` object.

## Calculate Final Price

Define a function `getOrderWithFinalPrice` to calculate the final price after applying discounts.

## Database Operations

Define a function `insertOrder` to insert an order into a database.

## Process Orders

Convert lines to orders, apply discounts, calculate final prices, and insert each order into the database.

## Error Handling and Cleanup

Handle exceptions, log errors, and close resources properly.

## Database Connection Management

Create a singleton object `ConnectionManager` to manage database connections.
