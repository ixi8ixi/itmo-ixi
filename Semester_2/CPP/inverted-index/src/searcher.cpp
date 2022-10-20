#include "searcher.h"

#include <algorithm>
#include <iostream>

namespace {

void skipWS(std::istream & stream)
{
    char c;
    while (std::isspace(stream.peek()) != 0 && stream.get(c)) {
    }
}

void getWord(std::istream & stream, std::string & string)
{
    string.clear();
    char c;
    while (std::isspace(stream.peek()) == 0 && stream.peek() != '"' && stream.get(c)) {
        string.push_back(c);
    }
}

void trimWord(Searcher::Word & word)
{
    auto it = std::find_if(word.begin(), word.end(), [](char c) { return std::isalnum(static_cast<unsigned char>(c)) != 0; });
    word.erase(word.begin(), it);
    it = std::find_if(word.rbegin(), word.rend(), [](char c) { return std::isalnum(static_cast<unsigned char>(c)) != 0; }).base();
    word.erase(it, word.end());
    std::transform(word.begin(), word.end(), word.begin(), [](char c) { return std::tolower(static_cast<unsigned char>(c)); });
}

} // anonymous namespace

// ------------------------------------------------------------------------------------------------
// WordTerm

Searcher::DocIterator::WordTerm::WordTerm(const Searcher * searcher, const Searcher::Word & word)
    : it(searcher->dictionary.docBegin(word))
    , end(searcher->dictionary.docEnd(word))
{
}

void Searcher::DocIterator::WordTerm::next(Position lowerBound)
{
    if (it->first >= lowerBound) {
        return;
    }
    std::size_t l = 0;
    std::size_t r = end - it;

    while (l < r - 1) {
        std::size_t m = (l + r) / 2;
        if ((it + m)->first < lowerBound) {
            l = m;
        }
        else {
            r = m;
        }
    }

    it += r;
}
bool Searcher::DocIterator::WordTerm::isEnd() const
{
    return it == end;
}
Searcher::ID Searcher::DocIterator::WordTerm::getCurrentId() const
{
    return it->first;
}

const Searcher::PositionSet & Searcher::DocIterator::WordTerm::getPositions() const
{
    return it->second;
}

bool Searcher::DocIterator::WordTerm::containsPosition(Searcher::Position pos) const
{
    if (Searcher::DocIterator::WordTerm::isEnd()) {
        return false;
    }
    return it->second.find(pos) != it->second.end();
}

std::size_t Searcher::DocIterator::WordTerm::getSize() const
{
    return it->second.size();
}

// ================================================================================================
// Doc Iterator

Searcher::DocIterator::DocIterator()
    : searcher(nullptr)
{
}

Searcher::DocIterator::DocIterator(const std::string & query, const Searcher * searcher)
    : searcher(searcher)
{
    splitQuery(query);
    setMaxId();
    if (!isEnd()) {
        alignRange(0, words.size());
        alignPhrases();
        alignQuery();
    }
}

const Searcher::Filename & Searcher::DocIterator::operator*() const
{
    return searcher->documents[currentId];
}

Searcher::DocIterator::pointer Searcher::DocIterator::operator->() const
{
    return &operator*();
}

Searcher::DocIterator & Searcher::DocIterator::operator++()
{
    ++currentId;
    alignQuery();
    return *this;
}

Searcher::DocIterator Searcher::DocIterator::operator++(int)
{
    auto result = *this;
    operator++();
    return result;
}

bool operator==(const Searcher::DocIterator & a, const Searcher::DocIterator & b)
{
    return (a.searcher == nullptr && b.searcher == nullptr) ||
            (a.searcher == b.searcher && a.currentId == b.currentId);
}

bool operator!=(const Searcher::DocIterator & a, const Searcher::DocIterator & b)
{
    return !(a == b);
}

bool Searcher::DocIterator::isEnd() const
{
    return searcher == nullptr;
}

void Searcher::DocIterator::splitQuery(const std::string & query)
{
    std::stringstream stream(query);
    Word word;
    bool phrase = false;
    std::size_t quote = 0;

    skipWS(stream);
    while (!stream.eof()) {
        if (stream.peek() == '"') {
            if (phrase) {
                if (words.size() - quote > 1) {
                    phrases.push_back(std::make_pair(quote, words.size()));
                }
            }
            else {
                quote = words.size();
            }
            phrase = !phrase;
            stream.get();
            skipWS(stream);
        }
        else {
            getWord(stream, word);
            trimWord(word);
            if (!word.empty()) {
                words.emplace_back(searcher, word);
            }
        }
        skipWS(stream);
    }

    if (phrase) {
        throw BadQuery("Query contains unmatched quotes");
    }

    if (words.empty()) {
        throw BadQuery("Empty phrase");
    }
}

void Searcher::DocIterator::setMaxId()
{
    currentId = 0;
    for (const auto & w : words) {
        if (w.isEnd()) {
            searcher = nullptr;
            return;
        }
        currentId = std::max(currentId, w.getCurrentId());
    }
}

void Searcher::DocIterator::alignQuery()
{
    std::size_t word = 0;
    std::size_t cphrase = 0;
    std::size_t sizeCheck = 0;

    while (!isEnd() && sizeCheck < words.size()) {
        if (!phrases.empty() && phrases[cphrase].first == word) {
            auto phrase = phrases[cphrase];
            if (words[phrase.first].getCurrentId() < currentId) {
                sizeCheck = 0;
                nextPhrase(phrase);
            }
            else {
                cphrase = cphrase + 1 < phrases.size() ? cphrase + 1 : 0;
                word = phrase.second < words.size() ? phrase.second : 0;
                sizeCheck += (phrase.second - phrase.first);
            }
        }
        else {
            ID wordId = words[word].getCurrentId();
            if (wordId == currentId) {
                ++sizeCheck;
                word = word + 1 < words.size() ? word + 1 : 0;
            }
            else if (wordId < currentId) {
                words[word].next(currentId);
                if (words[word].isEnd()) {
                    searcher = nullptr;
                }
            }
            else {
                sizeCheck = 0;
                currentId = wordId;
            }
        }
    }
}

void Searcher::DocIterator::alignRange(std::size_t from, std::size_t to)
{
    std::size_t current = from;
    std::size_t sizeCheck = 0;
    const std::size_t length = to - from;

    while (!words[current].isEnd() && sizeCheck < length) {
        ID wordId = words[current].getCurrentId();
        if (wordId == currentId) {
            ++sizeCheck;
            current = current + 1 < to ? current + 1 : from;
        }
        else if (wordId < currentId) {
            words[current].next(currentId);
        }
        else {
            sizeCheck = 0;
            currentId = wordId;
        }
    }

    if (sizeCheck < length) {
        searcher = nullptr;
    }
}

void Searcher::DocIterator::alignPhrases()
{
    if (!isEnd()) {
        for (const auto & p : phrases) {
            alignRange(p.first, p.second);
            while (!isEnd() && !checkDocument(p)) {
                ++currentId;
                alignRange(p.first, p.second);
            }
        }
    }
}

bool Searcher::DocIterator::checkDocument(const std::pair<std::size_t, std::size_t> & phrase) const
{
    std::size_t index = phrase.first;
    std::size_t minSize = words[phrase.first].getSize();
    for (std::size_t i = phrase.first; i < phrase.second; ++i) {
        if (words[i].getSize() < minSize) {
            index = i;
            minSize = words[i].getSize();
        }
    }

    auto first = words[index].getPositions().begin();
    auto last = words[index].getPositions().end();

    while (first != last) {
        if (checkLine(phrase, index, *first)) {
            return true;
        }
        else {
            ++first;
        }
    }
    return false;
}

bool Searcher::DocIterator::checkLine(const std::pair<std::size_t, std::size_t> & phrase,
                                      std::size_t index,
                                      Searcher::Position start) const
{
    start -= index - phrase.first;
    for (auto it = words.begin() + phrase.first; it != words.begin() + phrase.second; ++it) {
        if (!it->containsPosition(start++)) {
            return false;
        }
    }
    return true;
}

void Searcher::DocIterator::nextPhrase(const std::pair<std::size_t, std::size_t> & phrase)
{
    do {
        alignRange(phrase.first, phrase.second);
        if (!isEnd() && !checkDocument(phrase)) {
            ++currentId;
        }
        else {
            return;
        }
    } while (true);
}

// ================================================================================================
// Bad Query

Searcher::BadQuery::BadQuery(const std::string & message)
    : message(message)
{
}

const char * Searcher::BadQuery::what() const noexcept
{
    return message.c_str();
}

// ================================================================================================
// Searcher

void Searcher::add_document(const Searcher::Filename & filename, std::istream & strm)
{
    if (documents.contains(filename)) {
        remove_document(filename);
    }
    ID id = documents.topId();
    documents.addDocument(filename);

    Position position = 0;
    Word currentWord;
    while (strm >> currentWord) {
        trimWord(currentWord);
        if (!currentWord.empty()) {
            dictionary.addToDict(currentWord, id, position++);
        }
    }
}

void Searcher::remove_document(const Searcher::Filename & filename)
{
    if (documents.contains(filename)) {
        ID id = documents.removeDocument(filename);
        dictionary.removeDoc(id);
    }
}

std::pair<Searcher::DocIterator, Searcher::DocIterator> Searcher::search(const std::string & query) const
{
    return std::make_pair(DocIterator(query, this), DocIterator());
}
