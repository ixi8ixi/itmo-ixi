#include "searcher.h"

#include <algorithm>

// ================================================================================================
// Document storage

std::size_t Searcher::DocumentStorage::size() const
{
    return storage.size();
}

Searcher::ID Searcher::DocumentStorage::topId()
{
    return freeIds.empty() ? storage.size() : *freeIds.begin();
}

void Searcher::DocumentStorage::addDocument(const Searcher::Filename & path)
{
    if (!freeIds.empty()) {
        auto id = *freeIds.begin();
        freeIds.erase(freeIds.begin());
        storage[id] = path;
    }
    else {
        storage.push_back(path);
    }
}

Searcher::ID Searcher::DocumentStorage::removeDocument(const Searcher::Filename & path)
{
    bool found = false;
    ID result;
    for (std::size_t i = 0; i < storage.size(); ++i) {
        if (freeIds.find(i) == freeIds.end() && storage[i] == path) {
            found = true;
            result = i;
            break;
        }
    }

    if (!found) {
        return storage.size();
    }

    freeIds.insert(result);
    return result;
}

const Searcher::Filename & Searcher::DocumentStorage::operator[](Searcher::ID id) const
{
    return storage[id];
}

bool Searcher::DocumentStorage::contains(const Filename & filename) const
{
    for (std::size_t i = 0; i < storage.size(); ++i) {
        if (freeIds.find(i) != freeIds.end()) {
            continue;
        }

        if (storage[i] == filename) {
            return true;
        }
    }
    return false;
}

// ================================================================================================
// Dictionary

std::size_t Searcher::Dictionary::size() const
{
    return dictionary.size();
}

bool Searcher::Dictionary::contains(const Searcher::Word & word) const
{
    return dictionary.find(word) != dictionary.end();
}

Searcher::IdToPositions::iterator Searcher::Dictionary::findId(const Word & word, Searcher::ID id)
{
    return std::find_if(dictionary[word].begin(),
                        dictionary[word].end(),
                        [&](const std::pair<ID, PositionSet> & p) { return p.first == id; });
}

void Searcher::Dictionary::addToDict(const Searcher::Word & word,
                                     Searcher::ID id,
                                     Searcher::Position position)
{
    if (word.empty()) {
        return;
    }
    if (dictionary.find(word) == dictionary.end()) {
        dictionary.insert(std::make_pair(word, IdToPositions()));
    }

    if (findId(word, id) == dictionary[word].end()) {
        dictionary[word].emplace_back(id, PositionSet());
    }

    findId(word, id)->second.emplace(position);
}

void Searcher::Dictionary::removeDoc(ID id)
{
    auto pair = dictionary.begin();
    while (pair != dictionary.end()) {
        auto it = std::find_if(pair->second.begin(),
                               pair->second.end(),
                               [&](const std::pair<ID, PositionSet> & p) { return p.first == id; });
        if (it != pair->second.end()) {
            pair->second.erase(it);
        }
        auto check = pair;
        ++pair;
        if (check->second.empty()) {
            dictionary.erase(check);
        }
    }
}

Searcher::IdIterator Searcher::Dictionary::docBegin(const Word & word) const
{
    if (dictionary.find(word) == dictionary.end()) {
        return emptyIterator;
    }
    return dictionary.at(word).begin();
}

Searcher::IdIterator Searcher::Dictionary::docEnd(const Word & word) const
{
    if (dictionary.find(word) == dictionary.end()) {
        return emptyIterator;
    }
    return dictionary.at(word).end();
}
