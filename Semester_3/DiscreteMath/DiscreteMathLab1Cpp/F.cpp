#include <iostream>
#include <vector>
#include <set>
#include <queue>

int mainn() {
    std::set<std::size_t> vx;
    std::deque<std::size_t> code;
    std::multiset<std::size_t> sorted_code;

    std::size_t N;

    std::cin >> N;
    std::size_t ce;
    for (auto i = 0; i < N -2; ++i) {
        std::cin >> ce;
        code.push_back(ce);
        sorted_code.insert(ce);
    }

    ce = 1;
    auto it = sorted_code.begin();
    while (ce <= N) {
        if (it != sorted_code.end()) {
            if (ce < *it) {
                vx.insert(ce++);
            } else {
                std::size_t curr = *it;
                while (it != sorted_code.end() && curr == *it) {
                    ++it;
                }
                ++ce;
            }
        } else {
            vx.insert(ce++);
        }
    }

    for (std::size_t i = 0; i < N - 2; ++i) {
        std::cout << *vx.begin() << " " << code.front() << std::endl;
        vx.erase(vx.begin());
        sorted_code.erase(sorted_code.find(code.front()));
        if (sorted_code.find(code.front()) == sorted_code.end()) {
            vx.insert(code.front());
        }
        code.pop_front();
    }

    auto r = vx.begin();
    std::cout << *r++ << " " << *r;
}

