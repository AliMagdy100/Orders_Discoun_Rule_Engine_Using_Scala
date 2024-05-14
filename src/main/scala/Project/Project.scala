import java.io.{BufferedWriter, File, FileWriter}


import scala.io.{BufferedSource, Source}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime
import scala.math._
import java.sql.{Connection, Date, DriverManager, PreparedStatement}
import scala.util.{Try, Success, Failure}


object Project extends App {

  // Open log file for writing
  val logFile = new File("rules_engine.log")
  val writer = new BufferedWriter(new FileWriter(logFile, true))

  def logEvent(logLevel: String, message: String): Unit = {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val logMessage = s"$timestamp $logLevel $message"
    writer.write(logMessage + "\n")
    writer.flush()
  }

  // Logging the start of the application
  logEvent("INFO", "Order Discount Rule Engine started")

  //Reading the data into List of String
  logEvent("INFO", "Start Reading the order details data ")
  val source: BufferedSource = Source.fromFile("src/main/Resources/TRX1000.csv")
  val lines: List[String] = source.getLines().drop(1).toList // drop header
  //Creating the needed case classes
  case class Order(purchase_date: LocalDate, product_name: String, expiry_date: LocalDate, quantity: Int, unit_price: Double, channel: String, payment_method: String)
  case class OrderWithDiscount(purchase_date: LocalDate, product_name: String, expiry_date: LocalDate, quantity: Int, unit_price: Double, channel: String, payment_method: String,discount:Double)
  case class OrderWithFinalPrice(purchase_date: LocalDate, product_name: String, expiry_date: LocalDate, quantity: Int, unit_price: Double, channel: String, payment_method: String, discount: Double, final_price:Double)

  //Creating function for converting the string into order object
  def toOrder(line: String): Order = {
    val part = line.split(",")
    //processing timestamp
    val purchasingFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val Purchasing_Date = LocalDate.parse(part(0).substring(0, 10), purchasingFormatter)
    //processing expiration date
    val expirationFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val expirationDate = LocalDate.parse(part(2), expirationFormatter)

    Order(Purchasing_Date, part(1), expirationDate, part(3).toInt, part(4).toDouble, part(5), part(6))
  }

  //Set of Qualification and Calculation Functions
  def Q_expired(order: Order): Boolean = {

    //calculating days difference
    val Days_between = ChronoUnit.DAYS.between(order.purchase_date, order.expiry_date).toInt
    //Returning Boolean value
    Days_between < 30

  }

  def C_expired(order: Order): Double = {
    //calculating days difference
    val Days_between = ChronoUnit.DAYS.between(order.purchase_date, order.expiry_date).toInt
    val discount = Days_between match {
      case d if d >= 0 && d <= 29 => (30 - d) / 100.00 // Calculate discount based on remaining days
    }
    discount
  }

  def Q_cheeseOrWine(order: Order): Boolean = {
    val productCategory = order.product_name.split("-")(0).trim
    productCategory == "Cheese" || productCategory == "Wine"
  }

  def C_cheeseOrWine(order: Order): Double = {

    val productCategory = order.product_name.split("-")(0).trim

    productCategory match {
      case "Cheese" => 0.10 // 10% discount for Cheese
      case "Wine" => 0.05 // 5% discount for Wine

    }
  }

  def Q_specialDate(order: Order): Boolean = {
    order.purchase_date.getMonthValue == 3 && order.purchase_date.getDayOfMonth == 23 // Special date: 23rd of March
  }

  def C_specialDate(order: Order): Double = {
    0.5
  }

  def Q_quantityCount(order: Order): Boolean = {
    order.quantity > 5
  }

  def C_quantityCount(order: Order): Double = {
    order.quantity match {
      case q if q >= 6 && q <= 9 => 0.05 // 5% discount for 6 to 9 units
      case q if q >= 10 && q <= 14 => 0.07 // 7% discount for 10 to 14 units
      case q if q > 15 => 0.10 // 10% discount for more than 15 units
    }
  }

  def Q_isApp(order: Order): Boolean = {
    val channel = order.channel
    channel == "App"
  }

  def C_isApp(order: Order): Double ={
      val roundedQuantity = ceil(order.quantity.toDouble / 5.00) * 5 // Round up to the nearest multiple of 5
      roundedQuantity match {
        case q if q >= 1 && q <= 5 => 0.05 // 5% discount for quantities between 1 and 5
        case q if q >= 6 && q <= 10 => 0.10 // 10% discount for quantities between 6 and 10
        case q if q >= 11 && q <= 15 => 0.15 // 15% discount for quantities between 11 and 15
        case q if q > 15 => 0.20 // 20% discount for quantities greater than 15
      }
    }

  def Q_isVisa(order: Order): Boolean = {
    val channel = order.payment_method
    channel == "Visa"
  }

  def C_isVisa(order: Order): Double = {
    0.05
  }

  //Registering Each Qualification and Calculation Function
  val DiscountRule: List[(Function1[Order, Boolean], Function1[Order, Double])] =
      List(
        (Q_expired, C_expired),
        (Q_cheeseOrWine, C_cheeseOrWine),
        (Q_specialDate, C_specialDate),
        (Q_quantityCount, C_specialDate),
        (Q_isApp, C_isApp),
        (Q_isVisa, C_isVisa)
      )

  // Creating a new object with the extra discount attribute
  def getOrderWithDiscount(order: Order, rulesList: List[(Order => Boolean, Order => Double)]): OrderWithDiscount = {
      // Collect discount amounts based on the provided discount rules
      val discountAmounts = rulesList.collect {
        case (qualificationFunction, calculationFunction) if qualificationFunction(order) =>
          calculationFunction(order)
      }

      // Determine the result based on the number of discount amounts collected
      val discount = discountAmounts match {
        case none if none.isEmpty => 0.0 // No discount amounts collected
        case single if single.length == 1 => single.head // Only one discount amount collected
        case _ =>
          // Sort discount amounts in descending order
          val sortedDiscounts = discountAmounts.sorted(Ordering[Double].reverse)

          // Take the top two highest discount amounts
          val topTwoDiscounts = sortedDiscounts.take(2)

          // Calculate the average of the top two discount amounts
          val averageOfTopTwoDiscounts = topTwoDiscounts.sum / 2.0

          averageOfTopTwoDiscounts
      }
      OrderWithDiscount(order.purchase_date,order.product_name, order.purchase_date, order.quantity, order.unit_price, order.channel, order.payment_method,discount)
    }
  // Creating a new object with the extra final price attribute
  def getOrderWithFinalPrice(order:OrderWithDiscount):OrderWithFinalPrice ={
      val discount= order.discount
      val finalPrice= order.quantity * order.unit_price - order.quantity  * order.unit_price* order.discount
      OrderWithFinalPrice(order.purchase_date,order.product_name, order.expiry_date, order.quantity, order.unit_price, order.channel, order.payment_method,discount,finalPrice)
    }

  // Define the insertOrder function to insert an order into the database
  def insertOrder(connection: Connection, order: OrderWithFinalPrice): Unit = {
    val sql =
      """
        |INSERT INTO orders (purchase_date, product_name, expiry_date, quantity, unit_price, channel, payment_method, discount, final_price)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
      """.stripMargin

    var preparedStatement: PreparedStatement = null

    try {
      preparedStatement = connection.prepareStatement(sql)

      // Set parameter values for the PreparedStatement based on the order object
      preparedStatement.setDate(1, java.sql.Date.valueOf(order.purchase_date))
      preparedStatement.setString(2, order.product_name)
      preparedStatement.setDate(3, java.sql.Date.valueOf(order.expiry_date))
      preparedStatement.setInt(4, order.quantity)
      preparedStatement.setDouble(5, order.unit_price)
      preparedStatement.setString(6, order.channel)
      preparedStatement.setString(7, order.payment_method)
      preparedStatement.setDouble(8, order.discount)
      preparedStatement.setDouble(9, order.final_price)

      // Execute the INSERT statement
      preparedStatement.executeUpdate()
      println("Order inserted successfully")
    } catch {
      case e: Exception => println(s"Failed to insert order: ${e.getMessage}")
    } finally {
      // Close the PreparedStatement (connection will be closed separately)
      if (preparedStatement != null) {
        preparedStatement.close()
      }
    }
  }

  // Convert lines to orders and insert each order into the database

  try {
    lines.map(toOrder).map(order => getOrderWithDiscount(order, DiscountRule)).map(getOrderWithFinalPrice).foreach { order =>
      try {
        insertOrder(ConnectionManager.getConnection, order)
      } catch {
        case e: Exception =>
          logEvent("ERROR", s"Failed to process order: ${e.getMessage}")
      }
    }

    // Log the end of the application
    logEvent("INFO", "RuleEngine completed")
  } catch {
    case e: Exception =>
      logEvent("ERROR", s"Unexpected error: ${e.getMessage}")
  } finally {
    // Close resources
    source.close()
    writer.close()
    ConnectionManager.closeConnection()
  }
}

//Creating a singleton object for the connection
object ConnectionManager {
  // Database connection parameters
  private val url = "jdbc:oracle:thin:@//localhost:1521/XE"
  private val username = "scala"
  private val password = "password"

  // Lazy-initialized database connection only when needed
  lazy val connection: Connection = {
    // Attempt to establish the database connection
    Try {
      DriverManager.getConnection(url, username, password)
    } match {
      case Success(conn) =>
        println("Database connection established successfully")
        conn
      case Failure(exception) =>
        println(s"Failed to establish database connection: ${exception.getMessage}")
        throw exception
    }
  }

  // Method to retrieve the database connection
  def getConnection: Connection = connection
  // Method to close the database connection
  def closeConnection(): Unit = {
    try {
      if (connection != null && !connection.isClosed) {
        connection.close()
      }
    } catch {
      case e: Exception =>
        println(s"Failed to close database connection: ${e.getMessage}")
    }
  }
}

