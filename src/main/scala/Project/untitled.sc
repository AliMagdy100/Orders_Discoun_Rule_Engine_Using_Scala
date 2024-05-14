import java.io.{File, FileOutputStream, PrintWriter}
import java.util.Date
import scala.io.{BufferedSource, Source}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val x = "Wine - Prosecco Valdobienne"
x.split("-")(0).trim


def Q_isApp(order: Order): Boolean = {
  val channel = order.channel
  channel == "App"
}

def C_isApp(order: Order): Double = {
  val roundedQuantity = ceil(order.quantity.toDouble / 5.00) * 5 // Round up to the nearest multiple of 5
  roundedQuantity match {
    case q if q >= 1 && q <= 5 => 0.05 // 5% discount for quantities between 1 and 5
    case q if q >= 6 && q <= 10 => 0.10 // 10% discount for quantities between 6 and 10
    case q if q >= 11 && q <= 15 => 0.15 // 15% discount for quantities between 11 and 15
    case q if q > 15 => 0.20 // 20% discount for quantities greater than 15
  }
}
