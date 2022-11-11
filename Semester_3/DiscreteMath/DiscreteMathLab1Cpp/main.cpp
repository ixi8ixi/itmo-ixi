#include <iostream>
#include <vector>
#include <set>
#include <queue>

int main() {
    std::size_t S;
    std::cin >> S;

    std::deque<std::size_t> seq;
    std::deque<std::size_t> vx;

    for (std::size_t i = 0; i < S - 2; ++i) {
        std::size_t e;
        std::cin >> e;
        seq.push_back(e);
        vx.push_back(i + 1);
    }
    vx.push_back(S - 1);
    vx.push_back(S);

//    for (std::size_t i = 0; i < S - 2; ++i) {
//
//    }

    auto vit = vx.begin();
    auto sit = seq.begin();

    while (*vit >= *sit) {
        if (*vit == *sit) {
            ++vit;
            ++sit;
        } else if (*vit > *sit) {
            ++sit;
        }
    }

    std::cout << *vit << " <---- Cringe " << std::endl;
}
