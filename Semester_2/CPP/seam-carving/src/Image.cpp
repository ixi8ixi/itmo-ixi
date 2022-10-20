#include "Image.h"

Image::Image(std::vector<std::vector<Image::Pixel>> table)
    : m_table(std::move(table))
{
}

Image::Pixel::Pixel(int red, int green, int blue)
    : m_red(red)
    , m_green(green)
    , m_blue(blue)
{
}

Image::Pixel Image::GetPixel(size_t columnId, size_t rowId) const
{
    return m_table[columnId][rowId];
}

std::size_t Image::GetWidth() const
{
    return m_table.empty() || m_table[0].empty() ? 0 : m_table.size();
}

std::size_t Image::GetHeight() const
{
    return !m_table.empty() ? m_table[0].size() : 0;
}
