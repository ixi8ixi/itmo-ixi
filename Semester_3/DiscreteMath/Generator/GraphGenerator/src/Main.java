public class Main {
    public static void main(String[] args) {
        MyGraph graph;
        double c = 3;
        for (int size = 10; size < 200; size++) {
            long number = 0;
            for (int i = 0; i < 100; i++) {
                graph = new MyGraph(size);
                graph.generate(c);
                if (graph.containsTriangle()) {
                    number++;
                }
            }
            System.out.println(size + " " + number);
        }
    }
}
