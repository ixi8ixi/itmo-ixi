#include "primitives.h"

#include <fstream>

namespace rbtree {

// ================================================================================================
// rbtree::PointSet

PointSet::PointSet(const std::string & filename)
    : s_points(std::make_shared<std::set<Point>>())
{
    std::ifstream input(filename);
    if (input.is_open()) {
        double x;
        double y;
        while (input >> x >> y) {
            put(Point(x, y));
        }
    }
}

bool PointSet::empty() const
{
    return s_points->empty();
}

std::size_t PointSet::size() const
{
    return s_points->size();
}

void PointSet::put(const Point & point)
{
    s_points->insert(point);
}

bool PointSet::contains(const Point & point) const
{
    return s_points->find(point) != s_points->end();
}

std::pair<PointSet::iterator, PointSet::iterator> PointSet::range(const Rect & rect) const
{
    PointSet range;
    for (auto it = this->begin(); it != this->end(); ++it) {
        if (rect.contains(*it)) {
            range.put(*it);
        }
    }
    return {range.begin(), range.end()};
}

PointSet::iterator PointSet::begin() const
{
    return PointSet::iterator(s_points, s_points->begin());
}

PointSet::iterator PointSet::end() const
{
    return PointSet::iterator(s_points, s_points->end());
}

std::optional<Point> PointSet::nearest(const Point & point) const
{
    if (this->size() == 0) {
        return std::nullopt;
    }
    auto result = this->begin();
    double check_distance = point.distance(*result);
    for (auto it = this->begin(); it != this->end(); ++it) {
        double current_distance = point.distance(*it);
        if (current_distance < check_distance) {
            result = it;
            check_distance = current_distance;
        }
    }
    return *result;
}

std::pair<PointSet::iterator, PointSet::iterator> PointSet::nearest(const Point & p, std::size_t k) const
{
    if (k >= s_points->size()) {
        return std::make_pair(this->begin(), this->end());
    }
    if (k == 0) {
        return std::make_pair(this->end(), this->end());
    }

    PointDistanceQueue queue(k);
    for (auto it = begin(); it != end(); ++it) {
        queue.push(std::make_pair(p.distance(*it), &*it));
    }

    PointSet nearest;
    for (auto it = queue.begin(); it != queue.end(); ++it) {
        nearest.put(*it->second);
    }
    return std::make_pair(nearest.begin(), nearest.end());
}

std::ostream & operator<<(std::ostream & ostream, const PointSet & pointSet)
{
    for (const Point & p : pointSet) {
        ostream << p << std::endl;
    }
    return ostream;
}

// ================================================================================================
// rbtree::PointSet::iterator

PointSet::iterator::iterator()
    : s_it(std::set<Point>::iterator())
{
}

PointSet::iterator::iterator(const std::shared_ptr<std::set<Point>> & pointSet, std::set<Point>::iterator it)
    : s_pointset(pointSet)
    , s_it(it)
{
}

const Point & PointSet::iterator::operator*() const
{
    return *s_it;
}

PointSet::iterator::pointer PointSet::iterator::operator->() const
{
    return &*s_it;
}

PointSet::iterator & PointSet::iterator::operator++()
{
    ++s_it;
    return *this;
}

PointSet::iterator PointSet::iterator::operator++(int)
{
    iterator res = *this;
    operator++();
    return res;
}

bool operator==(const PointSet::iterator & itf, const PointSet::iterator & its)
{
    return itf.s_it == its.s_it;
}

bool operator!=(const PointSet::iterator & itf, const PointSet::iterator & its)
{
    return itf.s_it != its.s_it;
}

} // namespace rbtree

namespace kdtree {

// ================================================================================================
// kdtree::PointSet

PointSet::PointSet(const std::string & filename)
    : s_size(0)
{
    std::ifstream input(filename);
    if (input.is_open()) {
        double x;
        double y;
        while (input >> x >> y) {
            put(Point(x, y));
        }
    }
}

bool kdtree::PointSet::empty() const
{
    return s_size == 0;
}

std::size_t PointSet::size() const
{
    return s_size;
}

void PointSet::put(const Point & point)
{
    if (empty()) {
        s_root = std::make_shared<Node>(point,
                                        Rect(Point(DOUBLE_MIN, DOUBLE_MIN),
                                             Point(DOUBLE_MAX, DOUBLE_MAX)),
                                        nullptr);
        s_begin = s_root.get();
    }
    else {
        std::size_t depth = 0;
        Node * current = s_root.get();
        while (true) {
            if (current->value == point) {
                return;
            }
            if (depthCompare(depth, point, current)) {
                if (!current->left) {
                    Rect left = splitByPoint(current->range, current->value, depth).first;
                    current->left = std::make_shared<Node>(point, left, current);
                    s_begin = current == s_begin ? current->left.get() : s_begin;
                    break;
                }
                current = current->left.get();
            }
            else {
                if (!current->right) {
                    Rect right = splitByPoint(current->range, current->value, depth).second;
                    current->right = std::make_shared<Node>(point, right, current);
                    break;
                }
                current = current->right.get();
            }
            ++depth;
        }
    }
    ++s_size;
}

bool PointSet::contains(const Point & point) const
{
    Node * current = s_root.get();
    std::size_t depth = 0;
    while (current) {
        if (current->value == point) {
            return true;
        }
        std::pair<double, double> cords =
                depth % 2 == 0 ? std::make_pair(point.x(), current->value.x()) : std::make_pair(point.y(), current->value.y());
        current = cords.first < cords.second ? current->left.get() : current->right.get();
    }
    return false;
}

std::pair<PointSet::iterator, PointSet::iterator> PointSet::range(const Rect & rect) const
{
    PointSet range;
    rangeImpl(range, this->s_root.get(), rect);
    return std::make_pair(range.begin(), range.end());
}

PointSet::iterator PointSet::begin() const
{
    return size() > 0 ? iterator(s_begin, s_root) : iterator();
}

PointSet::iterator PointSet::end() const
{
    return iterator();
}

std::optional<Point> PointSet::nearest(const Point & point) const
{
    if (this->empty()) {
        return std::nullopt;
    }

    return *nearest(point, 1).first;
}

std::pair<PointSet::iterator, PointSet::iterator> PointSet::nearest(const Point & p, std::size_t k) const
{
    if (k >= s_size) {
        return std::make_pair(this->begin(), this->end());
    }
    if (k == 0) {
        return std::make_pair(this->end(), this->end());
    }

    PointDistanceQueue queue(k);
    nearestImpl(p, 0, queue, s_root.get());

    PointSet nearest;
    for (auto it = queue.begin(); it != queue.end(); ++it) {
        nearest.put(*it->second);
    }
    return std::make_pair(nearest.begin(), nearest.end());
}

// return true if point's coordinate is less than node's
bool PointSet::depthCompare(std::size_t depth, const Point & point, const Node * node)
{
    return depth % 2 == 0 ? point.x() < node->value.x() : point.y() < node->value.y();
}

std::pair<Rect, Rect> PointSet::splitByPoint(const Rect & rect, const Point & point, std::size_t depth)
{
    if (depth % 2 == 0) {
        return std::make_pair(
                Rect(Point(rect.xmin(), rect.ymin()), Point(point.x(), rect.ymax())),
                Rect(Point(point.x(), rect.ymin()), Point(rect.xmax(), rect.ymax())));
    }
    else {
        return std::make_pair(
                Rect(Point(rect.xmin(), rect.ymin()), Point(rect.xmax(), point.y())),
                Rect(Point(rect.xmin(), point.y()), Point(rect.xmax(), rect.ymax())));
    }
}

// Recursive range implementation
void PointSet::rangeImpl(PointSet & ps, Node * current, const Rect & rect)
{
    if (rect.contains(current->value)) {
        ps.put(current->value);
    }

    if (current->left && rect.intersects(current->left->range)) {
        rangeImpl(ps, current->left.get(), rect);
    }
    if (current->right && rect.intersects(current->right->range)) {
        rangeImpl(ps, current->right.get(), rect);
    }
}

// Implementation of recursive algorithm of searching of the nearest neighbor in kd-tree
// 'point' - Point that we need to find nearest neighbor
// 'nearest' - pair of current nearest Point and distance between it and 'point'
// 'current' - current node
void PointSet::nearestImpl(const Point & point, std::size_t depth, PointDistanceQueue & nearest, Node * current)
{
    nearest.push(std::make_pair(point.distance(current->value), &current->value));
    std::shared_ptr<Node> first;
    std::shared_ptr<Node> second;
    if (depthCompare(depth, point, current)) {
        first = current->left;
        second = current->right;
    }
    else {
        first = current->right;
        second = current->left;
    }
    if (first && first->range.distance(point) <= nearest.maxValue()) {
        nearestImpl(point, depth + 1, nearest, first.get());
    }
    if (second && second->range.distance(point) <= nearest.maxValue()) {
        nearestImpl(point, depth + 1, nearest, second.get());
    }
}

// ================================================================================================
// kdtree::PointSet::Node

kdtree::PointSet::Node::Node(const Point & p, const Rect & rect, Node * parent)
    : value(p)
    , range(rect)
    , parent(parent)
{
}

// ================================================================================================
// kdtree::PointSet::iterator

PointSet::iterator::iterator()
    : current(nullptr)
{
}

PointSet::iterator::iterator(PointSet::Node * node, const std::shared_ptr<Node> & root)
    : current(node)
    , root(root)
{
}

const Point & kdtree::PointSet::iterator::operator*() const
{
    return current->value;
}

PointSet::iterator::pointer PointSet::iterator::operator->() const
{
    return &current->value;
}

PointSet::iterator & PointSet::iterator::operator++()
{
    if (current->right) {
        current = current->right.get();
        while (current->left) {
            current = current->left.get();
        }
    }
    else {
        while (current->parent && current->parent->right.get() == current) {
            current = current->parent;
        }
        current = current->parent;
    }
    return *this;
}

PointSet::iterator PointSet::iterator::operator++(int)
{
    iterator res = *this;
    operator++();
    return res;
}

bool operator==(const PointSet::iterator & itf, const PointSet::iterator & its)
{
    return (itf.current == nullptr && its.current == nullptr) ||
            (itf.current != nullptr && its.current != nullptr && *itf == *its);
}

bool operator!=(const PointSet::iterator & itf, const PointSet::iterator & its)
{
    return !operator==(itf, its);
}

std::ostream & operator<<(std::ostream & ostream, const PointSet & pointSet)
{
    for (const Point & p : pointSet) {
        ostream << p << std::endl;
    }
    return ostream;
}

} // namespace kdtree