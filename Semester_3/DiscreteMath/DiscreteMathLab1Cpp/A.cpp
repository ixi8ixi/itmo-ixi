#include <iostream>
#include <vector>
#include <fstream>
#include <deque>

class Graph {
public:
    explicit Graph(std::size_t size) {
        edges.resize(size);
        for (std::size_t i = 0; i < size; ++i) {
            edges[i].resize(size);
        }
    }

    void connect(std::size_t from, std::size_t to) {
        edges[from][to] = true;
        edges[to][from] = true;
    }

    bool isConnected(std::size_t from, std::size_t to) const {
        return edges[from][to];
    }
private:
    std::vector<std::vector<bool>> edges;
};

int maiin() {
    std::size_t N;
    std::cin >> N;

    std::string line;
    std::getline(std::cin, line);

//    auto t1 = std::chrono::high_resolution_clock::now();
    Graph graph(N);
//    auto t2 = std::chrono::high_resolution_clock::now();
//    std::chrono::duration<double> diff = t2 - t1;
//    std::cout << diff.count() << std::endl;

    for (std::size_t i = 0; i < N; ++i) {
        std::getline(std::cin, line);
        for (std::size_t j = 0; j < i; ++j) {
            if (line[j] == '1') {
                graph.connect(i, j);
            }
        }
    }

    std::deque<std::size_t> queue;

    for (std::size_t i = 0; i < N; ++i) {
        queue.push_back(i);
    }

    for (std::size_t k = 0; k < N * (N - 1); ++k) {
        if (graph.isConnected(queue[0], queue[1])) {
            std::size_t f = queue[0];
            queue.pop_front();
            queue.push_back(f);
        } else {
            std::size_t vIndex;
            for (std::size_t i = 2;; ++i) {
                if (graph.isConnected(queue[0], queue[i]) && graph.isConnected(queue[1], queue[i + 1])) {
                    vIndex = i;
                    break;
                }
            }

            std::size_t j = 0;
            while (1 + j < vIndex - j) {
                std::size_t b = queue[1 + j];
                queue[1 + j] = queue[vIndex - j];
                queue[vIndex - j] = b;
                ++j;
            }

            std::size_t f = queue[0];
            queue.pop_front();
            queue.push_back(f);
        }
    }

    for (std::size_t & it : queue) {
        std::cout << (it + 1) << " ";
    }
}
