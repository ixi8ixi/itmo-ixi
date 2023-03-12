#include <iostream>
#include <set>
#include <vector>

class Polynomial {
public:
    explicit Polynomial(int d)
        : deg(d)
    {}

    void addVector(const std::vector<long> & v) {
        for (int i = deg; i >= 0; --i) {
            coefficients[i] += v[i];
        }
    }

    void addFull(std::size_t vxn) {
        switch (vxn) {
            case 1:
                addVector(ONE);
                break;
            case 2:
                addVector(TWO);
                break;
            case 3:
                addVector(THREE);
                break;
            case 4:
                addVector(FOUR);
                break;
            case 5:
                addVector(FIVE);
                break;
            case 6:
                addVector(SIX);
                break;
            case 7:
                addVector(SEVEN);
                break;
            case 8:
                addVector(EIGHT);
                break;
            case 9:
                addVector(NINE);
                break;
            case 10:
                addVector(TEN);
                break;
            default:
                addVector(ONE);
        }
    }

    friend std::ostream &operator<<(std::ostream &os, const Polynomial &polynomial);

    static const std::vector<long> ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
private:
    const int deg;
    std::vector<long> coefficients = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
};

const std::vector<long> Polynomial::ONE = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
const std::vector<long> Polynomial::TWO = {0, -1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
const std::vector<long> Polynomial::THREE = {0, 2, -3, 1, 0, 0, 0, 0, 0, 0, 0};
const std::vector<long> Polynomial::FOUR = {0, -6, 11, -6, 1, 0, 0, 0, 0, 0, 0};
const std::vector<long> Polynomial::FIVE = {0, 24, -50, 35, -10, 1, 0, 0, 0, 0, 0};
const std::vector<long> Polynomial::SIX = {0, -120, 274, -225, 85, -15, 1, 0, 0, 0, 0};
const std::vector<long> Polynomial::SEVEN = {0, 720, -1764, 1624, -735, 175, -21, 1, 0, 0, 0};
const std::vector<long> Polynomial::EIGHT = {0, -5040, 13068, -13132, 6769, -1960, 322, -28, 1, 0, 0};
const std::vector<long> Polynomial::NINE = {0, 40320, -109584, 118124, -67284, 22449, -4536, 546, -36, 1, 0};
const std::vector<long> Polynomial::TEN = {0, -362880, 1026576, -1172700, 723680, -269325, 63273, -9450, 870, -45, 1};

std::ostream &operator<<(std::ostream &os, const Polynomial &polynomial) {
    for (int i = polynomial.deg; i >= 0; --i) {
        os << polynomial.coefficients[i] << " ";
    }
    return os;
}

class Graph{
public:
    explicit Graph(std::size_t n)
        :vxn(n)
    {
        table.resize(vxn);
        for (std::size_t i = 0; i < vxn; ++i) {
            table[i].resize(vxn);
        }
    }

    Graph mergeVx(std::size_t a, std::size_t b) const {
        Graph result(vxn - 1);
        // add edges from the highest vertex
        for (std::size_t i = 0; i < vxn; ++i) {
            for (std::size_t j = 0; j < i; ++j) {
                std::size_t ni, nj;
                // ni and nj
                // cringe
            }
        }
    }

    void addEdge(std::size_t from, std::size_t to) {
        table[from][to] = true;
        table[to][from] = true;
        ++edgn;
    }

    // use only if graph is not full
    std::pair<std::size_t, std::size_t> findPair() {
        for (std::size_t i = 0; i < vxn; ++i) {
            for (std::size_t j = 0; j < i; ++j) {
                if (!table[i][j]) {
                    return std::make_pair(i, j);
                }
            }
        }
    }

    bool isFull() const {
        return edgn == (vxn * (vxn - 1)) / 2;
    }

    const std::size_t vxn;
private:
    std::size_t edgn = 0;
    std::vector<std::vector<bool>> table;
};

void count(Polynomial & p, Graph g) {
    if (g.isFull()) {
        p.addFull(g.vxn);
    } else {
        auto [a, b] = g.findPair();
        count(p, g.mergeVx(a, b));
        g.addEdge(a, b);
        count(p, g);
    }
}

int main() {
    int s = 3;
    Polynomial polynomial(3);
    Graph graph(3);
    graph.addEdge(0, 1);
    graph.addEdge(0, 2);
    graph.addEdge(1, 2);
    count(polynomial, graph);
    std::cout << polynomial << std::endl;
}
