#include "Image.h"
#include "SeamCarver.h"

#include <fstream>
#include <iostream>

namespace {

std::vector<std::vector<Image::Pixel>> ReadImageFromCSV(std::ifstream & input)
{
    size_t width, height;
    input >> width >> height;
    std::vector<std::vector<Image::Pixel>> table;
    for (size_t columnId = 0; columnId < width; ++columnId) {
        std::vector<Image::Pixel> column;
        for (size_t rowId = 0; rowId < height; ++rowId) {
            size_t red, green, blue;
            input >> red >> green >> blue;
            column.emplace_back(red, green, blue);
        }
        table.emplace_back(std::move(column));
    }
    return table;
}

void WriteImageToCSV(const SeamCarver & carver, std::ofstream & output)
{
    const size_t width = carver.GetImageWidth();
    const size_t height = carver.GetImageHeight();
    output << width << " " << height << "\n";
    const Image & image = carver.GetImage();
    for (size_t columnId = 0; columnId < width; ++columnId) {
        for (size_t rowId = 0; rowId < height; ++rowId) {
            const Image::Pixel & pixel = image.GetPixel(columnId, rowId);
            output << pixel.m_red << " " << pixel.m_green << " " << pixel.m_blue << std::endl;
        }
    }
}

// TESTS
#define TEST(test_name)                                                               \
    struct test_name                                                                  \
    {                                                                                 \
        bool end;                                                                     \
        bool result;                                                                  \
                                                                                      \
        test_name()                                                                   \
        {                                                                             \
            end = true;                                                               \
            result = true;                                                            \
        }                                                                             \
                                                                                      \
        ~test_name()                                                                  \
        {                                                                             \
            std::cout << #test_name << " : "                                          \
                      << "[ " << (result ? "  OK  " : "FAILED") << " ]" << std::endl; \
        }                                                                             \
    };                                                                                \
    for (test_name test; test.end; test.end = false)

#define EQ_ASSERT(sample, expression)                                    \
    if (sample != expression) {                                          \
        std::cout << "Expected equality of: " << std::endl               \
                  << "    " << #expression << std::endl                  \
                  << "        Which is ---> " << expression << std::endl \
                  << "    and sample" << std::endl                       \
                  << "        Which is ---> " << sample << std::endl;    \
        test.result = false;                                             \
    }

bool isEmptySeam(const std::vector<std::size_t> & seam)
{
    const bool result = seam.empty();
    if (!result) {
        std::cout << "Empty seam expected" << std::endl;
    }
    return result;
}

void runTests()
{
    std::cout << "===== RUN TESTS =====" << std::endl;

    TEST(Test_0x0)
    {
        std::vector<std::vector<Image::Pixel>> image;

        SeamCarver carver(std::move(image));
        auto vertical = carver.FindVerticalSeam();
        auto horizontal = carver.FindHorizontalSeam();

        test.result = test.result && isEmptySeam(vertical);
        test.result = test.result && isEmptySeam(horizontal);

        EQ_ASSERT(0, carver.GetImageHeight());
        EQ_ASSERT(0, carver.GetImageWidth());
    };

    TEST(Test_3x0)
    {
        std::vector<std::vector<Image::Pixel>> image;
        std::vector<Image::Pixel> col0 = {};
        std::vector<Image::Pixel> col1 = {};
        std::vector<Image::Pixel> col2 = {};

        image.push_back(col0);
        image.push_back(col1);
        image.push_back(col2);

        SeamCarver carver(std::move(image));

        auto vertical = carver.FindVerticalSeam();
        auto horizontal = carver.FindHorizontalSeam();

        test.result = test.result && isEmptySeam(vertical);
        test.result = test.result && isEmptySeam(horizontal);

        EQ_ASSERT(0, carver.GetImageHeight());
        EQ_ASSERT(0, carver.GetImageWidth());
    }

    TEST(Test_1x1)
    {
        std::vector<std::vector<Image::Pixel>> image;
        std::vector<Image::Pixel> col0 = {Image::Pixel(1, 1, 1)};

        image.push_back(col0);

        SeamCarver carver(std::move(image));
        auto vertical = carver.FindVerticalSeam();
        auto horizontal = carver.FindHorizontalSeam();

        carver.RemoveVerticalSeam(vertical);

        EQ_ASSERT(1, vertical.size());
        EQ_ASSERT(1, horizontal.size());
        EQ_ASSERT(0, carver.GetImageHeight());
        EQ_ASSERT(0, carver.GetImageWidth());
    }

    TEST(Test_1x3)
    {
        std::vector<std::vector<Image::Pixel>> image;
        std::vector<Image::Pixel> col0 = {Image::Pixel(1, 1, 1),
                                          Image::Pixel(2, 2, 2),
                                          Image::Pixel(3, 3, 3)};
        image.push_back(col0);

        SeamCarver carver(std::move(image));

        auto vertical = carver.FindVerticalSeam();
        auto horizontal = carver.FindHorizontalSeam();

        carver.RemoveVerticalSeam(vertical);

        EQ_ASSERT(3, vertical.size());
        EQ_ASSERT(1, horizontal.size());
        EQ_ASSERT(0, carver.GetImageHeight());
        EQ_ASSERT(0, carver.GetImageWidth());
        EQ_ASSERT(0, vertical[0]);
        EQ_ASSERT(0, vertical[1]);
        EQ_ASSERT(0, vertical[2]);
    }

    TEST(Test_3x1)
    {
        std::vector<std::vector<Image::Pixel>> image;
        std::vector<Image::Pixel> col0 = {Image::Pixel(1, 1, 1)};
        std::vector<Image::Pixel> col1 = {Image::Pixel(2, 2, 2)};
        std::vector<Image::Pixel> col2 = {Image::Pixel(3, 3, 3)};

        image.push_back(col0);
        image.push_back(col1);
        image.push_back(col2);

        SeamCarver carver(std::move(image));
        auto vertical = carver.FindVerticalSeam();
        auto horizontal = carver.FindHorizontalSeam();

        carver.RemoveVerticalSeam(vertical);

        EQ_ASSERT(1, vertical.size());
        EQ_ASSERT(3, horizontal.size());
        EQ_ASSERT(1, carver.GetImageHeight());
        EQ_ASSERT(2, carver.GetImageWidth());
    }

    TEST(Test_5x3)
    {
        std::vector<std::vector<Image::Pixel>> image;

        std::vector<Image::Pixel> col0 = {Image::Pixel(124, 224, 168),
                                          Image::Pixel(134, 183, 186),
                                          Image::Pixel(98, 100, 113)};

        std::vector<Image::Pixel> col1 = {Image::Pixel(34, 137, 202),
                                          Image::Pixel(160, 145, 152),
                                          Image::Pixel(145, 147, 233)};

        std::vector<Image::Pixel> col2 = {Image::Pixel(92, 57, 234),
                                          Image::Pixel(168, 15, 102),
                                          Image::Pixel(29, 220, 158)};

        std::vector<Image::Pixel> col3 = {Image::Pixel(134, 121, 39),
                                          Image::Pixel(207, 85, 3),
                                          Image::Pixel(140, 120, 122)};

        std::vector<Image::Pixel> col4 = {Image::Pixel(178, 193, 226),
                                          Image::Pixel(131, 150, 214),
                                          Image::Pixel(132, 37, 173)};

        image.push_back(col0);
        image.push_back(col1);
        image.push_back(col2);
        image.push_back(col3);
        image.push_back(col4);

        SeamCarver carver(std::move(image));
        auto vertical = carver.FindVerticalSeam();

        carver.RemoveVerticalSeam(vertical);

        EQ_ASSERT(4, carver.GetImageWidth());
        EQ_ASSERT(3, carver.GetImageHeight());
        EQ_ASSERT(3, vertical.size());
        EQ_ASSERT(4, vertical[0]);
        EQ_ASSERT(3, vertical[1]);
        EQ_ASSERT(4, vertical[2]);
    }

    TEST(Test_7x6)
    {
        std::vector<std::vector<Image::Pixel>> image;

        std::vector<Image::Pixel> col0 = {Image::Pixel(87, 151, 238),
                                          Image::Pixel(9, 60, 62),
                                          Image::Pixel(245, 250, 27),
                                          Image::Pixel(190, 113, 226),
                                          Image::Pixel(144, 151, 2),
                                          Image::Pixel(181, 72, 61)};

        std::vector<Image::Pixel> col1 = {Image::Pixel(173, 222, 229),
                                          Image::Pixel(98, 152, 32),
                                          Image::Pixel(33, 25, 74),
                                          Image::Pixel(130, 127, 45),
                                          Image::Pixel(129, 85, 132),
                                          Image::Pixel(235, 142, 78)};

        std::vector<Image::Pixel> col2 = {Image::Pixel(115, 47, 44),
                                          Image::Pixel(49, 171, 59),
                                          Image::Pixel(129, 127, 185),
                                          Image::Pixel(7, 150, 34),
                                          Image::Pixel(135, 86, 89),
                                          Image::Pixel(41, 207, 61)};

        std::vector<Image::Pixel> col3 = {Image::Pixel(68, 93, 216),
                                          Image::Pixel(10, 140, 183),
                                          Image::Pixel(30, 203, 33),
                                          Image::Pixel(12, 217, 198),
                                          Image::Pixel(138, 35, 199),
                                          Image::Pixel(128, 123, 155)};

        std::vector<Image::Pixel> col4 = {Image::Pixel(4, 255, 227),
                                          Image::Pixel(78, 226, 210),
                                          Image::Pixel(158, 12, 10),
                                          Image::Pixel(133, 204, 36),
                                          Image::Pixel(70, 214, 85),
                                          Image::Pixel(54, 133, 237)};

        std::vector<Image::Pixel> col5 = {Image::Pixel(178, 49, 12),
                                          Image::Pixel(202, 19, 79),
                                          Image::Pixel(80, 223, 197),
                                          Image::Pixel(19, 69, 152),
                                          Image::Pixel(94, 168, 246),
                                          Image::Pixel(228, 94, 219)};

        std::vector<Image::Pixel> col6 = {Image::Pixel(232, 169, 12),
                                          Image::Pixel(103, 55, 86),
                                          Image::Pixel(171, 7, 119),
                                          Image::Pixel(188, 39, 202),
                                          Image::Pixel(19, 166, 234),
                                          Image::Pixel(5, 224, 167)};

        image.push_back(col0);
        image.push_back(col1);
        image.push_back(col2);
        image.push_back(col3);
        image.push_back(col4);
        image.push_back(col5);
        image.push_back(col6);

        SeamCarver carver(std::move(image));
        auto vertical = carver.FindVerticalSeam();

        EQ_ASSERT(6, vertical.size());
        EQ_ASSERT(2, vertical[0]);
        EQ_ASSERT(2, vertical[1]);
        EQ_ASSERT(2, vertical[2]);
        EQ_ASSERT(2, vertical[3]);
        EQ_ASSERT(2, vertical[4]);
        EQ_ASSERT(2, vertical[5]);

        carver.RemoveVerticalSeam(vertical);

        EQ_ASSERT(6, carver.GetImageWidth());

        vertical = carver.FindVerticalSeam();

        EQ_ASSERT(1, vertical[0]);
        EQ_ASSERT(2, vertical[1]);
        EQ_ASSERT(2, vertical[2]);
        EQ_ASSERT(1, vertical[3]);
        EQ_ASSERT(2, vertical[4]);
        EQ_ASSERT(3, vertical[5]);
    }

    std::cout << std::endl;
}

#undef EQ_ASSERT
#undef TEST

} // anonymous namespace

int main(int argc, char * argv[]) // data/tower.csv data/tower_updated.csv
{
    runTests();

    // Check command line arguments
    const size_t expectedAmountOfArgs = 3;
    if (argc != expectedAmountOfArgs) {
        std::cout << "Wrong amount of arguments. Provide filenames as arguments. See example below:\n";
        std::cout << "seam-carving data/tower.csv data/tower_updated.csv" << std::endl;
        return 0;
    }
    // Check csv file
    std::ifstream inputFile(argv[1]);
    if (!inputFile.good()) {
        std::cout << "Can't open source file " << argv[1] << ". Verify that the file exists." << std::endl;
    }
    else {
        auto imageSource = ReadImageFromCSV(inputFile);
        SeamCarver carver(std::move(imageSource));
        std::cout << "Image: " << carver.GetImageWidth() << "x" << carver.GetImageHeight() << std::endl;
        const size_t pixelsToDelete = 150;
        for (size_t i = 0; i < pixelsToDelete; ++i) {
            std::vector<size_t> seam = carver.FindVerticalSeam();
            carver.RemoveVerticalSeam(seam);
            std::cout << "width = " << carver.GetImageWidth() << ", height = " << carver.GetImageHeight() << std::endl;
        }
        std::ofstream outputFile(argv[2]);
        WriteImageToCSV(carver, outputFile);
        std::cout << "Updated image is written to " << argv[2] << "." << std::endl;
    }
    return 0;
}
