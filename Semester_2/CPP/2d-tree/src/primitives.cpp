#include "primitives.h"

#include <algorithm>
#include <cmath>

// ================================================================================================
// Point

Point::Point(double x, double y)
    : s_x(x)
    , s_y(y)
{
}

double Point::x() const
{
    return s_x;
}

double Point::y() const
{
    return s_y;
}

double Point::distance(const Point & point) const
{
    return std::hypot(this->x() - point.x(), this->y() - point.y());
}

bool Point::operator<(const Point & point) const
{
    return x() < point.x() || (x() == point.x() && y() < point.y());
}

bool Point::operator>(const Point & point) const
{
    return point < *this;
}

bool Point::operator<=(const Point & point) const
{
    return *this == point || *this < point;
}

bool Point::operator>=(const Point & point) const
{
    return point <= *this;
}

bool Point::operator==(const Point & point) const
{
    return x() == point.x() && y() == point.y();
}

bool Point::operator!=(const Point & point) const
{
    return !(point == *this);
}

std::ostream & operator<<(std::ostream & out, const Point & point)
{
    out << "(" << point.x() << ", " << point.y() << ")";
    return out;
}

// ================================================================================================
// Rectangle

Rect::Rect(const Point & left_bottom, const Point & right_top)
    : s_lb(left_bottom)
    , s_rt(right_top)
{
}

double Rect::xmin() const
{
    return s_lb.x();
}

double Rect::ymin() const
{
    return s_lb.y();
}

double Rect::xmax() const
{
    return s_rt.x();
}

double Rect::ymax() const
{
    return s_rt.y();
}

double Rect::distance(const Point & p) const
{
    if (this->contains(p)) {
        return 0;
    }

    double dx = std::max({s_lb.x() - p.x(), p.x() - s_rt.x(), 0.0});
    double dy = std::max({s_lb.y() - p.y(), p.y() - s_rt.y(), 0.0});
    return std::hypot(dx, dy);
}

bool Rect::contains(const Point & p) const
{
    return s_lb.x() <= p.x() &&
            s_rt.x() >= p.x() &&
            s_lb.y() <= p.y() &&
            s_rt.y() >= p.y();
}

bool Rect::intersects(const Rect & rect) const
{
    return !(ymax() < rect.ymin() || xmax() < rect.xmin() || ymin() > rect.ymax() || xmin() > rect.xmax());
}

std::ostream & operator<<(std::ostream & ostream, const Rect & rect)
{
    ostream << "[" << rect.s_lb << "   " << rect.s_rt << "]" << std::endl;
    return ostream;
}

// ================================================================================================
// Priority queue for points

PointDistanceQueue::PointDistanceQueue(std::size_t full)
    : s_max(full)
{
    points.reserve(full);
}

bool PointDistanceQueue::empty() const
{
    return points.empty();
}

bool PointDistanceQueue::full() const
{
    return points.size() == s_max;
}

std::size_t PointDistanceQueue::size() const
{
    return points.size();
}

void PointDistanceQueue::push(const entry & e)
{
    if (!full()) {
        points.push_back(e);
        std::push_heap(points.begin(), points.end(), pairComp());
        return;
    }
    if (e.first < maxValue()) {
        std::pop_heap(points.begin(), points.end(), pairComp());
        points.pop_back();
        points.push_back(e);
        std::push_heap(points.begin(), points.end(), pairComp());
    }
}

double PointDistanceQueue::maxValue() const
{
    return size() > 0 ? points.front().first : DOUBLE_MAX;
}

PointDistanceQueue::iterator PointDistanceQueue::begin()
{
    return points.begin();
}

PointDistanceQueue::iterator PointDistanceQueue::end()
{
    return points.end();
}