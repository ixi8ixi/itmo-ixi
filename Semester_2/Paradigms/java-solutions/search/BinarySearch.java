package search;

public class BinarySearch {
    // Pre: args - массив целых чисел, записаных строками &&
    // args.length > 0 &&
    // для любых 0 <= a, b <= args.length   args[a] >= args[b]
    // (args[args.length] - виртуальное значение меньшее любого числа,
    // args[0'] - виртуальное занчение большее любого ичсла)

    // Post: 0' <= R <= args.length - 1
    // (1) R == 0 && args.length == 1 ||
    // (2) args[R + 1] <= args[0] && args[R] > args[0]

    static int iterativeBinarySearch(String[] args) {
        // (2)
        // Заметим, что R существует, т.к. args[args.length] <= args[0],
        // тогда 0 <= R <= args.length - 1

        // (2)
        // 0 < R + 1 &&
        // R + 1 <= args.length &&
        // args[R + 1] <= args[0]

        int x = Integer.parseInt(args[0]);
        // (1) args.length == 1
        // (2)
        // 0 < R + 1 &&
        // R + 1 <= args.length &&
        // args[R + 1] <= x

        int left = 0;
        // (2)
        // left' < R + 1 &&
        // R + 1 <= args.length &&
        // args[R + 1] <= x

        int right = args.length;
        // (1)
        // left' = 0 &&
        // right' = 1

        // (1) 0 + 1 < 1 -> цикл не выполняется

        // (2)
        // I: left' < R + 1 &&
        // R + 1 <= right' &&
        // args[R + 1] <= x

        while (left + 1 < right) {
            // (2)
            // left' < R + 1 &&
            // R + 1 <= right' &&
            // args[R + 1] <= x &&
            // left' + 1 < right'

            int middle = (left + right) / 2;
            // докажем, что left' < middle < right'

            // I. Предположим, что left' >= middle
            // 2 * left' >= 2 * middle
            // 2 * left' >= left' + right'
            // (т.к. middle = floor((left' + right') / 2), что можно переписать как
            // middle = (left' + right') / 2, если (left' + right') % 2 == 0,
            // middle = (left' + right' - 1) / 2, если (left' + right') % 2 == 1)
            // left' >= right', но
            // right' > left' + 1, следовательно
            // left' > left' + 1 - противоречие

            // II. Предположим, что middle >= right'
            // 2 * middle >= 2 * right'
            // left' + right' - 1 >= 2 * right' (по описанным выше причинам)
            // left' - 1 >= right', но
            // right' > left' + 1
            // left' - 1 >= left' + 1 - противоречие

            // (2)
            // left' < R + 1 &&
            // R + 1 <= right' &&
            // args[R + 1] <= x &&
            // left' + 1 < right' &&
            // left' < middle' < right'

            if (Integer.parseInt(args[middle]) <= x) {
                // (2)
                // left' < R + 1 &&
                // R + 1 <= right' &&
                // args[R + 1] <= x &&
                // left' + 1 < right' &&
                // left' < middle < right' &&
                // args[middle] <= x

                right = middle;
                // (2)
                // докажем неравнство right' >= R + 1:
                // right' = middle, следовательно
                // args[middle] == args[right'], следовательно
                // args[right'] <= x, но R - наименьший индекс такой что args[R + 1] <= x, следовательно
                // right' >= R + 1

                // (2)
                // left' < R + 1 &&
                // R + 1 <= right' &&
                // args[R + 1] <= x
            } else {
                // (2)
                // left' < R + 1 &&
                // R + 1 <= right' &&
                // args[R + 1] <= x &&
                // left' + 1 < right' &&
                // left' < middle < right' &&
                // args[middle] > x

                left = middle;
                // (2)
                // Докажем неравенство left' < R + 1
                // left' = middle
                // args[middle] = args[left']
                // args[left'] > x
                // args[R + 1] <= x
                // left' < R + 1

                // (2)
                // left/ < R + 1 &&
                // R + 1 <= right' &&
                // args[R + 1] <= x
            }
            // (2)
            // left' < R + 1 &&
            // R + 1 <= right' &&
            // args[R + 1] <= x

            // Цикл однажды закончится, т.к left' < middle < right', следовательно
            // на каждой итерации цикла величина right' - left' уменьшается хотябы на 1
        }
        // (2)
        // left' < R + 1 &&
        // R + 1 <= right' &&
        // args[R + 1] <= x &&
        // left' + 1 >= right'

        // Докажем, что R + 1 = right'
        // R + 1 > left' && left' + 1 >= right' ->
        // R + 2 > right' или R + 2 >= right' + 1 ->
        // R + 1 >= right'
        // При этом R + 1 <= right' ->
        // R + 1 = right'

        // (1) right' - 1 = 0
        // (2) R = right' - 1 (0 <= R <= args.length)
        return right - 1;
    }


    //Pre: args - массив целых чисел, записаных строками &&
    // args.length > 0 &&
    // для любых 0 <= a, b <= args.length   args[a] >= args[b]
    // (args[args.length] - виртуальное значение меньшее любого числа,
    // args[0'] - виртуальное занчение большее любого числа) &&
    // 0' <= left <= args.length : args[left] < args[0] &&
    // args.length >= right > 0 : args[right] >= args[0]

    // Post: 0' <= R <= args.length - 1
    // (1) R == 0 && args.length == 1 ||
    // (2) args[R + 1] <= args[0] && args[R] > args[0]

    static int recursiveBinarySearch(String[] args, int left, int right) {
        // (1)
        // left = 0 (т.к. если left > 0, то args[left] <= args[0])
        // right = 1

        // (2)
        // Заметим, что R существует, т.к. args[args.length] <= args[0],
        // тогда 0 <= R <= args.length - 1
        // При этом left < R + 1 <= right

        // (2)
        // left < R + 1 &&
        // R + 1 <= right

        if (left + 1 < right) {
            // (2)
            // left < R + 1 &&
            // R + 1 <= right

            int middle = (left + right) / 2;
            // докажем, что left < middle < right

            // I. Предположим, что left >= middle
            // 2 * left >= 2 * middle
            // 2 * left >= left + right
            // (т.к. middle = floor((left + right) / 2), что можно переписать как
            // middle = (left + right) / 2, если (left + right) % 2 == 0,
            // middle = (left + right - 1) / 2, если (left + right) % 2 == 1)
            // left >= right, но
            // right > left + 1, следовательно
            // left > left + 1 - противоречие

            // II. Предположим, что middle >= right
            // 2 * middle >= 2 * right
            // left + right - 1 >= 2 * right (по описанным выше причинам)
            // left - 1 >= right, но
            // right > left + 1
            // left - 1 >= left + 1 - противоречие

            // (2)
            // left < R + 1 &&
            // R + 1 <= right &&
            // left < middle < right

            if (Integer.parseInt(args[middle]) <= Integer.parseInt(args[0])) {
                // (2)
                // left < R + 1 &&
                // R + 1 <= right &&
                // left < middle < right &&
                // args[middle] <= args[0]

                // (2)
                // Докажем, что
                // 1 <= middle <= args.length &&
                // args[middle] >= args[0]

                // 0 < left <= middle -> 1 <= middle
                // middle <= right <= args.length
                // args[middle] >= args[0] - уже выполняется

                return recursiveBinarySearch(args, left, middle);
                // По контракту вернёт искомое значение
            } else {
                // (2)
                // left < R + 1 &&
                // R + 1 <= right &&
                // left < middle < right &&
                // args[middle] > args[0]

                // (2)
                // Докажем, что
                // middle = 0 || middle > 0 && args[middle] > x

                // Очевидно, что middle > 0, т.к.
                // 0 <= left < middle
                // тогда middle > 0 && args[middle] > x

                return recursiveBinarySearch(args, middle, right);
                // По контракту вернёт искомое значение
            }
            // Заметим, что рекурсия не бесконечна, т.к
            // left < middle < right, следовательно
            // на каждой итерации величина right - left уменьшается хотябы на 1

        } else {
            // (1) left = 0 && right = 1
            // R = right - 1
            // R = 0

            // (2)
            // left < R + 1 &&
            // R + 1 <= right
            // left + 1 >= right

            // Докажем, что R + 1 = right
            // left + 1 < R + 2 && left + 1 >= right ->
            // left + 1 <= R + 1 -> right <= R + 1
            // но right >= R + 1 ->
            // right = R + 1

            return right - 1;
        }
    }

    // Pre: args - массив целых чисел, записаных строками &&
    // args.length > 0 &&
    // для любых 0 <= a, b <= args.length   args[a] >= args[b]

    // Post: 0 <= R <= args.length - 1
    // (1) R == 0 && args.length == 1 ||
    // (2) (R == 0 || args[R + 1] <= args[0] && args[R] > args[0])

    public static void main(String[] args) {
        // args.length > 0 &&
        // для любых 0 <= a, b <= args.length   args[a] >= args[b]
        int resOne = iterativeBinarySearch(args);
        // по контракту 0 <= R <= args.length - 1
        // (1) R == 0 && args.length == 1 ||
        // (2) (R == 0 || args[R + 1] <= args[0] && args[R] > args[0])

        // args.length > 0 &&
        // для любых 0 <= a, b <= args.length   args[a] >= args[b] &&
        // args[0] виртуальное значение > x &&
        // args[args.length] >= x
        int resTwo = recursiveBinarySearch(args, 0, args.length);
        // по контракту 0 <= R <= args.length - 1
        // (1) R == 0 && args.length == 1 ||
        // (2) (R == 0 || args[R + 1] <= args[0] && args[R] > args[0])

        assert resOne == resTwo;
        System.out.println(resOne);
    }
}
