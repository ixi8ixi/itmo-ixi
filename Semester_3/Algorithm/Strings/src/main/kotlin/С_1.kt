import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val sc = Scanner(System.`in`)
    val s = sc.nextLine()

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

    var t = 1
    while (t < s.length && !(z[t] >= s.length - t && s.length % t == 0)) {
        t++
    }
    if (t > s.length / 2) {
        println(s.length)
    } else {
        println(t)
    }
}