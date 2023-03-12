import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val sc = Scanner(System.`in`)
    val N = sc.nextInt()
    val M = sc.nextInt()

    val string = IntArray(N * 2)
    for (i in 0 until N) {
        string[i] = sc.nextInt()
        string[2 * N - 1 - i] = string[i]
    }
    val z = IntArray(N * 2)
    var left = 0
    var right = 0

    for (i in 1 until string.size - 1) {
        z[i] = max(0, min(right - i, z[i - left]))
        while (i + z[i] < string.size && string[z[i]] == string[i + z[i]]) {
            z[i]++
        }
        if (i + z[i] > right) {
            left = i
            right = i + z[i]
        }
    }

    print("$N ")

    for (i in 1..N / 2) {
        val pos = N * 2 - 2 * i
        if (i <= z[pos]) {
            print("${N - i} ")
        }
    }
}