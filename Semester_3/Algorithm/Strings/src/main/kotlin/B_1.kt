import java.util.Scanner
import kotlin.math.max
import kotlin.math.min

fun main() {
    val sc = Scanner(System.`in`)
    val p = sc.nextLine()
    val t = sc.nextLine()
    val s = "$p $t"

    val z = IntArray(s.length)
    var left = 0
    var right = 0

    for (i in 1 until s.length) {
        z[i] = max(0, min(right - i, z[i - left]))
        while (i + z[i] < s.length && s[z[i]] == s[i + z[i]]) {
            z[i]++
        }
        if (i + z[i] > right) {
            left = i
            right = i + z[i]
        }
    }

    var counter = 0
    val list = ArrayList<Int>()

    for (i in p.length until s.length) {
        if (z[i] == p.length) {
            counter++
            list.add(i - p.length)
        }
    }

    println(counter)
    for (i in list) {
        print("$i ")
    }
}