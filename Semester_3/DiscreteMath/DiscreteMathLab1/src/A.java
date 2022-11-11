import java.util.*;

class Graph {
    final boolean[][] edges;

    public Graph(int size) {
        edges = new boolean[size][size];
    }

    public void connect(int from, int to) {
        edges[from][to] = true;
        edges[to][from] = true;
    }

    public boolean isConnected(int from, int to) {
        return edges[from][to];
    }

    @Override
    public String toString() {
        return Arrays.deepToString(edges);
    }
}

public class A {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int N = Integer.parseInt(in.nextLine());

        Graph graph = new Graph(N);

        for (int i = 0; i < N; i++) {
            String line = in.nextLine();
            for (int j = 0; j < i; j++) {
                if (line.charAt(j) == '1') {
                    graph.connect(j, i);
                }
            }
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < N; i++) {
            queue.addLast(i);
        }

        for (int k = 0; k < N * (N - 1); k++) {
            int first = queue.removeFirst();
            int second = queue.getFirst();
            queue.addFirst(first);
            if (!graph.isConnected(first, second)) {
                ArrayDeque<Integer> smallQueue = new ArrayDeque<>();
                queue.removeFirst();
                smallQueue.addFirst(queue.removeFirst());
//                while () {
//
//                }
            }
            queue.addLast(queue.removeFirst());
        }

//        ArrayList<Integer> list = new ArrayList<>();
//        for (int i = 0; i < N; i++) {
//            list.add(i);
//        }
//
//        for (int k = 0; k < N * (N - 1); k++) {
//            if (graph.isConnected(list.get(0), list.get(1))) {
//                list.add(list.get(0));
//                list.remove(0);
//            } else {
//                int vIndex;
//                int first = list.get(0);
//                int second = list.get(1);
//                for (int i = 2;; i++) {
//                    if (graph.isConnected(first, list.get(i)) && graph.isConnected(second, list.get(i + 1))) {
//                        vIndex = i;
//                        break;
//                    }
//                }
//
//                int j = 0;
//                while (1 + j < vIndex - j) {
//                    int b = list.get(1 + j);
//                    list.set(1 + j, list.get(vIndex - j));
//                    list.set(vIndex - j, b);
//                    j++;
//                }
//
//                list.add(list.get(0));
//                list.remove(0);
//            }
//        }
//
//        for (int i = 0; i < N; i++) {
//            System.out.print((list.get(i) + 1) + " ");
//        }
    }
}
