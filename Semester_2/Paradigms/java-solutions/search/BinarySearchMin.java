package search;

public class BinarySearchMin {

    // Pre: args - массив целых чисел, записанных строками &&
    // существует 0 <= m < args.length :
    // для любого 0 <= a < m   args[a] > args[m] && для любого m < b < args.length   args[m] < args[b] &&
    // для любых 0 <= a < b < m   args[a] > args[b] && для любых m < c < d < args.length args[c] < args[d]

    // Post: R = args[m]
    // (censored) принадлежит args && для любого 0 <= i < args.length   R <= args[i]

    static int iterativeSearchMin(String[] args) {
        // -1 < m <= args.length
        int left = -1;

        // left < m <= args.length - 1
        int right = args.length - 1;

        // I: left' < m <= right'
        while (left + 1 < right) {
            // left' < m <= right' && left' + 1 < right'
            int middle = (left + right) / 2;
            // left' < m <= right' && left' + 1 < right' && middle = floor((left' + right') / 2)

            // Примечание: middle < right'
            // Пусть middle >= right'
            // floor((left' + right') / 2) >= right' ->
            // (left' + right') / 2 >= right' ->
            // left' + right' >= 2 * right' ->
            // left' >= right'
            // но left' + 1 < right' -> left' < right' - противоречие ->
            // middle < right'
            // поэтому middle < args.length - 1

            if (Integer.parseInt(args[middle]) < Integer.parseInt(args[middle + 1])) {
                // left' < m <= right' && left' + 1 < right' && middle = floor((left' + right') / 2) &&
                // args[middle] < args[middle + 1]

                // args[middle] < args[middle + 1] ->
                // middle >= m, т.к. иначе, если
                // middle < m, то middle + 1 <= m ->
                // args[middle] > args[middle + 1] - противоречие ->
                // left' < m <= middle
                right = middle;
                // right' = middle
                // left' < m <= right'

            } else {
                // left' < m <= right' && left' + 1 < right' && middle = floor((left' + right') / 2) &&
                // args[middle] >= args[middle + 1]

                // args[middle] >= args[middle + 1] ->
                // middle < m, т.к. иначе, если
                // middle >= m, middle + 1 > m ->
                // args[middle] < args[middle + 1] - противоречие ->
                // middle < m <= right'
                left = middle;
                // left' < m <= right'
            }
            // left' < m <= right'
        }
        // left' < m <= right' && left' + 1 >= right' ->
        // right' - 1 <= left' && left' < m <= right' ->
        // right' - 1 < m <= right' ->
        // right' = m
        return Integer.parseInt(args[right]);
    }

    // Pre: args - массив целых чисел, записанных строками &&
    // существует 0 <= m < args.length :
    // для любого 0 <= a < m   args[a] > args[m] && для любого m < b < args.length   args[m] < args[b] &&
    // для любых 0 <= a < b < m   args[a] > args[b] && для любых m < c < d < args.length args[c] < args[d] &&
    // left < m <= right

    // Post: R = args[m]
    static int recursiveSearchMin(String[] args, int left, int right) {
        // left < m <= right
        if (left + 1 >= right) {
            // left < m <= right && left + 1 >= right ->
            // right - 1 <= left && left < m <= right ->
            // right - 1 < m <= right ->
            // right = m
            return Integer.parseInt(args[right]);
        } else {
            // left < m <= right && left + 1 < right
            int middle = (left + right) / 2;
            // left < m <= right && left + 1 < right &&
            // middle = floor((left + right) / 2)

            // Примечание: middle < right
            // Пусть middle >= right
            // floor((left + right) / 2) >= right ->
            // (left + right) / 2 >= right ->
            // left + right >= 2 * right ->
            // left >= right
            // но left + 1 < right -> left < right - противоречие ->
            // middle < right
            // поэтому middle < args.length - 1

            if (Integer.parseInt(args[middle]) < Integer.parseInt(args[middle + 1])) {
                // left < m <= right && left + 1 < right &&
                // middle = floor((left + right) / 2) &&
                // args[middle] < args[middle + 1]

                // args[middle] < args[middle + 1] ->
                // middle >= m, т.к. иначе, если
                // middle < m, то middle + 1 <= m ->
                // args[middle] > args[middle + 1] - противоречие ->
                // left < m <= middle
                return recursiveSearchMin(args, left, middle);
                // по контракту вернёт решение
            } else {
                // left < m <= right && left + 1 < right &&
                // middle = floor((left + right) / 2) &&
                // args[middle] >= args[middle + 1]

                // args[middle] >= args[middle + 1] ->
                // middle < m, т.к. иначе, если
                // middle >= m, middle + 1 > m ->
                // args[middle] < args[middle + 1] - противоречие ->
                // middle < m <= right
                return recursiveSearchMin(args, middle, right);
                // по контракту вернёт решение
            }
        }
    }

    // Pre: args - массив целых чисел, записанных строками &&
    // существует 0 <= m < args.length :
    // для любого 0 <= a < m   args[a] > args[m] && для любого m < b < args.length   args[m] < args[b] &&
    // для любых 0 <= a < b < m   args[a] > args[b] && для любых m < c < d < args.length args[c] < args[d]

    // Post: R = args[m]
    public static void main(String[] args) {
        // args - массив целых чисел, записанных строками &&
        // существует 0 <= m < args.length :
        // для любого 0 <= a < m   args[a] > args[m] && для любого m < b < args.length   args[m] < args[b] &&
        // для любых 0 <= a < b < m   args[a] > args[b] && для любых m < c < d < args.length args[c] < args[d]
        int resOne = iterativeSearchMin(args);
        // по контракту R = args[m]

        // args - массив целых чисел, записанных строками &&
        // существует 0 <= m < args.length :
        // для любого 0 <= a < m   args[a] > args[m] && для любого m < b < args.length   args[m] < args[b] &&
        // для любых 0 <= a < b < m   args[a] > args[b] && для любых m < c < d < args.length args[c] < args[d] &&
        // -1 < m <= args.length - 1
        int resTwo = recursiveSearchMin(args, -1, args.length - 1);
        // по контракту R = args[m]
        assert resOne == resTwo;
        System.out.println(resOne);
    }
}
