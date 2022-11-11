#include <iostream>
#include <vector>
#include <set>
#include <queue>

struct Node {
    std::set<std::size_t> neighbours;
};

int maiiin() {
    std::size_t S;
    std::cin >> S;

    std::vector<Node> vx;
    vx.resize(S);
    for (std::size_t i = 0; i < S - 1; ++i) {
        std::size_t from, to;
        std::cin >> from >> to;

        --from;
        --to;
        vx[from].neighbours.insert(to);
        vx[to].neighbours.insert(from);
    }

    std::priority_queue<std::size_t, std::vector<size_t>, std::greater<>> leaves;

    for (std::size_t i = 0; i < S; ++i) {
        if (vx[i].neighbours.size() == 1) {
            leaves.push(i);
        }
    }

    for (std::size_t i = 0; i < S - 2; ++i) {
        std::size_t cl = leaves.top();
        leaves.pop();
        std::size_t nv = vx[cl].neighbours.begin().operator*();
        vx[nv].neighbours.erase(cl);
        if (vx[nv].neighbours.size() == 1) {
            leaves.push(nv);
        }
        std::cout << (nv + 1) << " ";
    }
}