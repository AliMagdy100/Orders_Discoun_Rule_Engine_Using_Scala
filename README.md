# Orders_Discoun_Rule_Engine_Using_Scala
 
# Order Discount Rule Engine

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
