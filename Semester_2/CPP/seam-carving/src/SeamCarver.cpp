#include "SeamCarver.h"

#include <algorithm>
#include <cmath>
#include <stdexcept>

SeamCarver::SeamCarver(Image image)
    : m_image(std::move(image))
{
    RefreshEnergyTable();
}

const Image & SeamCarver::GetImage() const
{
    return m_image;
}

size_t SeamCarver::GetImageWidth() const
{
    return m_image.GetWidth();
}

size_t SeamCarver::GetImageHeight() const
{
    return m_image.GetHeight();
}

double SeamCarver::CountDelta(const Image::Pixel & first, const Image::Pixel & second)
{
    double result = 0;
    result += std::pow(first.m_green - second.m_green, 2);
    result += std::pow(first.m_red - second.m_red, 2);
    result += std::pow(first.m_blue - second.m_blue, 2);
    return result;
}

double SeamCarver::GetPixelEnergy(size_t columnId, size_t rowId) const
{
    const Image::Pixel top = m_image.GetPixel(columnId, rowId > 0 ? rowId - 1 : GetImageHeight() - 1);
    const Image::Pixel bottom = m_image.GetPixel(columnId, rowId < GetImageHeight() - 1 ? rowId + 1 : 0);
    const Image::Pixel right = m_image.GetPixel(columnId < GetImageWidth() - 1 ? columnId + 1 : 0, rowId);
    const Image::Pixel left = m_image.GetPixel(columnId > 0 ? columnId - 1 : GetImageWidth() - 1, rowId);
    return std::sqrt(CountDelta(bottom, top) + CountDelta(right, left));
}

double SeamCarver::GetEnergyFT(std::size_t columnId, std::size_t rowId, Direction direction) const
{
    switch (direction) {
    case Direction::Horizontal:
        return m_energyTable[columnId][rowId];
    case Direction::Vertical:
        return m_energyTable[rowId][columnId];
    }
    throw std::invalid_argument("Invalid direction");
}

SeamCarver::Seam SeamCarver::FindSeam(Direction direction) const
{
    const auto [width, height] = GetWidthHeight(direction);

    if (height == 1) {
        return Seam(width);
    }
    if (width == 0 || height == 0) {
        return Seam();
    }

    std::vector<std::vector<double>> table;
    table.reserve(width);

    // Create the first column
    table.emplace_back();
    table[0].reserve(height);
    for (std::size_t i = 0; i < height; i++) {
        table[0].emplace_back(GetEnergyFT(0, i, direction));
    }

    // Create all remaining columns
    for (std::size_t i = 1; i < width; i++) {
        table.emplace_back();
        table[i].reserve(height);
        table[i].emplace_back(GetEnergyFT(i, 0, direction) + std::min(table[i - 1][0], table[i - 1][1]));
        for (std::size_t j = 1; j < height - 1; j++) {
            table[i].emplace_back(GetEnergyFT(i, j, direction) + std::min({table[i - 1][j], table[i - 1][j - 1], table[i - 1][j + 1]}));
        }
        table[i].emplace_back(GetEnergyFT(i, height - 1, direction) + std::min(table[i - 1][height - 1], table[i - 1][height - 2]));
    }

    // Finding minimal
    std::size_t minPos = std::min_element(table[width - 1].begin(), table[width - 1].end()) - table[width - 1].begin();

    // Create the seam
    Seam seam(width);
    seam[width - 1] = minPos;
    for (std::size_t i = width - 1; i > 0; i--) {
        std::size_t upper = seam[i] > 0 ? seam[i] - 1 : seam[i];
        std::size_t straight = seam[i];
        std::size_t lower = seam[i] < height - 1 ? seam[i] + 1 : seam[i];
        std::size_t next = table[i - 1][upper] <= table[i - 1][straight] ? upper : straight;
        next = table[i - 1][next] <= table[i - 1][lower] ? next : lower;
        seam[i - 1] = next;
    }
    return seam;
}

SeamCarver::Seam SeamCarver::FindHorizontalSeam() const
{
    return FindSeam(Direction::Horizontal);
}

SeamCarver::Seam SeamCarver::FindVerticalSeam() const
{
    return FindSeam(Direction::Vertical);
}

void SeamCarver::RemoveElement(std::size_t columnId, std::size_t rowId, Direction direction)
{
    switch (direction) {
    case Direction::Horizontal:
        m_image.m_table[columnId].erase(m_image.m_table[columnId].begin() + rowId);
        return;
    case Direction::Vertical:
        for (std::size_t i = rowId; i < GetImageWidth() - 1; i++) {
            m_image.m_table[i][columnId] = m_image.m_table[i + 1][columnId];
        }
        return;
    }
    throw std::invalid_argument("Invalid direction, Horizontal or Vertical expected");
}

std::pair<std::size_t, std::size_t> SeamCarver::GetWidthHeight(Direction direction) const
{
    switch (direction) {
    case Direction::Horizontal:
        return {GetImageWidth(), GetImageHeight()};
    case Direction::Vertical:
        return {GetImageHeight(), GetImageWidth()};
    }
    throw std::invalid_argument("Invalid direction, Horizontal or Vertical expected");
}

void SeamCarver::RemoveSeam(const Seam & seam, Direction direction)
{
    const auto [width, height] = GetWidthHeight(direction);

    if (height == 1) {
        m_image.m_table.clear();
        return;
    }
    if (width == 0 || height == 0) {
        return;
    }

    for (std::size_t i = 0; i < width; i++) {
        RemoveElement(i, seam[i], direction);
    }
    if (direction == Direction::Vertical) {
        m_image.m_table.pop_back();
    }

    RefreshEnergyTable();
}

void SeamCarver::RemoveHorizontalSeam(const Seam & seam)
{
    RemoveSeam(seam, Direction::Horizontal);
}

void SeamCarver::RemoveVerticalSeam(const Seam & seam)
{
    RemoveSeam(seam, Direction::Vertical);
}

void SeamCarver::RefreshEnergyTable()
{
    const std::size_t width = GetImageWidth();
    const std::size_t height = GetImageHeight();
    m_energyTable.clear();
    m_energyTable.reserve(width);
    for (std::size_t i = 0; i < width; i++) {
        m_energyTable.emplace_back();
        m_energyTable[i].reserve(height);
        for (std::size_t j = 0; j < height; j++) {
            m_energyTable[i].emplace_back(GetPixelEnergy(i, j));
        }
    }
}
