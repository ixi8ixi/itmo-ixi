#include <iostream>
#include <set>
#include <vector>

struct Node {
    explicit Node(std::vector<Node> * nd) {
        nodes = nd;
    }

    void setColor(std::size_t clr) {
        color = clr;
        for (unsigned long neighbour : neighbours) {
            nodes->at(neighbour).colors_list.insert(clr);
        }
    }

    std::size_t getMinColor() const {
        std::size_t r = 1;
        while (colors_list.find(r) != colors_list.end()) {
            ++r;
        }
        return r;
    }

    bool visited = false;
    std::size_t color = 0;
    std::set<std::size_t> colors_list;
    std::set<std::size_t> neighbours;
    std::vector<Node> * nodes;
};

void dfs(std::size_t v, std::vector<Node> * vector) {
    if (!vector->at(v).visited) {
        vector->at(v).visited = true;
        Node cv = vector->at(v);
        std::size_t color = cv.getMinColor();
        vector->at(v).setColor(color);
        for (unsigned long neighbour : cv.neighbours) {
            dfs(neighbour, vector);
        }
    }
}

int mainnnn() {
    std::vector<Node> nodes;
    std::size_t n, m;
    std::cin >> n >> m;

    for (std::size_t i = 0; i < n; ++i) {
        nodes.push_back(Node(&nodes));
    }

    std::size_t from, to;
    for (std::size_t i = 0; i < m; ++i) {
        std::cin >> from >> to;
        --from;
        --to;
        nodes[from].neighbours.insert(to);
        nodes[to].neighbours.insert(from);
    }

    std::size_t mdeg = 0;
    for (std::size_t i = 0; i < n; ++i) {
        mdeg = std::max(mdeg, nodes[i].neighbours.size());
    }
    mdeg = mdeg % 2 == 0 ? mdeg + 1 : mdeg;

    dfs(0, &nodes);

    std::cout << mdeg << std::endl;

    for (std::size_t i = 0; i < nodes.size(); ++i) {
        std::cout << nodes[i].color << std::endl;
    }
}
