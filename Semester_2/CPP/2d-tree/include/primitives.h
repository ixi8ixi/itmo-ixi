#pragma once

#include <limits>
#include <memory>
#include <optional>
#include <ostream>
#include <set>
#include <vector>

constexpr double DOUBLE_MIN = std::numeric_limits<double>::min();
constexpr double DOUBLE_MAX = std::numeric_limits<double>::max();

class Point
{
public:
    Point(double x, double y);

    double x() const;
    double y() const;
    double distance(const Point &) const;

    bool operator<(const Point &) const;
    bool operator>(const Point &) const;
    bool operator<=(const Point &) const;
    bool operator>=(const Point &) const;
    bool operator==(const Point &) const;
    bool operator!=(const Point &) const;

    friend std::ostream & operator<<(std::ostream &, const Point &);

private:
    const double s_x;
    const double s_y;
};

class Rect
{
public:
    Rect(const Point & left_bottom, const Point & right_top);

    double xmin() const;
    double ymin() const;
    double xmax() const;
    double ymax() const;
    double distance(const Point & p) const;

    bool contains(const Point & p) const;
    bool intersects(const Rect &) const;

    friend std::ostream & operator<<(std::ostream &, const Rect &);

private:
    const Point s_lb;
    const Point s_rt;
};

class PointDistanceQueue
{
public:
    using entry = std::pair<double, const Point *>;
    using iterator = std::vector<entry>::iterator;

    PointDistanceQueue() = delete;
    PointDistanceQueue(std::size_t full);

    bool empty() const;
    bool full() const;
    std::size_t size() const;
    void push(const entry &);
    double maxValue() const;

    iterator begin();
    iterator end();

private:
    const std::size_t s_max;
    std::vector<entry> points;
    struct pairComp
    {
        bool operator()(const entry & a, const entry & b) const
        {
            return a.first < b.first;
        }
    };
};

namespace rbtree {

class PointSet
{
public:
    class iterator
    {
    public:
        using iterator_category = std::forward_iterator_tag;
        using difference_type = std::ptrdiff_t;
        using value_type = Point;
        using pointer = const Point *;
        using reference = const Point &;

        iterator();
        iterator(const std::shared_ptr<std::set<Point>> & pointSet, std::set<Point>::iterator it);

        reference operator*() const;
        pointer operator->() const;
        iterator & operator++();
        iterator operator++(int);

        friend bool operator==(const iterator &, const iterator &);
        friend bool operator!=(const iterator &, const iterator &);

    private:
        std::shared_ptr<std::set<Point>> s_pointset;
        std::set<Point>::iterator s_it;
    };

    PointSet(const std::string & filename = {});

    bool empty() const;
    std::size_t size() const;
    void put(const Point &);
    bool contains(const Point &) const;

    std::pair<iterator, iterator> range(const Rect &) const;
    iterator begin() const;
    iterator end() const;

    std::optional<Point> nearest(const Point &) const;
    std::pair<iterator, iterator> nearest(const Point & p, std::size_t k) const;

    friend std::ostream & operator<<(std::ostream &, const PointSet &);

private:
    const std::shared_ptr<std::set<Point>> s_points;
};

} // namespace rbtree

namespace kdtree {

class PointSet
{
    struct Node
    {
        Point value;
        Rect range;

        Node * parent = nullptr;
        std::shared_ptr<Node> left;
        std::shared_ptr<Node> right;

        Node(const Point & point, const Rect & rect, Node * parent);
    };

public:
    class iterator
    {
    public:
        using iterator_category = std::forward_iterator_tag;
        using difference_type = std::ptrdiff_t;
        using value_type = Point;
        using pointer = const Point *;
        using reference = const Point &;

    private:
        Node * current = nullptr;
        std::shared_ptr<Node> root;

    public:
        iterator();
        iterator(Node * node, const std::shared_ptr<Node> & root);

        reference operator*() const;
        pointer operator->() const;
        iterator & operator++();
        iterator operator++(int);

        friend bool operator==(const iterator &, const iterator &);
        friend bool operator!=(const iterator &, const iterator &);
    };

    PointSet(const std::string & filename = {});

    bool empty() const;
    std::size_t size() const;
    void put(const Point &);
    bool contains(const Point &) const;

    std::pair<iterator, iterator> range(const Rect &) const;
    iterator begin() const;
    iterator end() const;

    std::optional<Point> nearest(const Point &) const;
    std::pair<iterator, iterator> nearest(const Point & p, std::size_t k) const;

    friend std::ostream & operator<<(std::ostream &, const PointSet &);

private:
    static bool depthCompare(std::size_t depth, const Point & point, const Node * node);
    static std::pair<Rect, Rect> splitByPoint(const Rect & rect, const Point & point, std::size_t depth);

    static void rangeImpl(PointSet & ps, Node * current, const Rect & rect);
    static void nearestImpl(const Point & point, std::size_t depth, PointDistanceQueue & nearest, Node * current);

    std::size_t s_size;
    std::shared_ptr<Node> s_root;
    Node * s_begin;
};

} // namespace kdtree7