package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {

    var inputImage = args[1]
    val outputImage = args[3]
    val newWidth = args[5].toInt()
    val newHeigt = args[7].toInt()


    var img = ImageIO.read(File(inputImage))

    img = removeVerticalSeam(img, newWidth)
    img = removeHorizontalSeam(img, newHeigt)

    ImageIO.write(img, "png", File(outputImage))
}

fun removeHorizontalSeam(img: BufferedImage,  reduceHeightBy: Int): BufferedImage {

    var width = img.width
    var height = img.height
    var img = img
    val newHeightValue = height - reduceHeightBy
    var imageList = imageToRGBList(img)

    while (height != newHeightValue) {
        val energies = countImageEnergy(img)

        val distances = createDistancesArrayHorizonatl(energies)
        val shortest = shortestPathHorizontal(distances)

        imageList = imageToRGBList(img)

        for (x in 0 until width) {

            for (y in shortest[x] until height - 1) {
                imageList[x][y] = imageList[x][y + 1]
            }

        }

        img = rgbListToImage(imageList)

        height --
    }
    var cutList: MutableList<MutableList<Int>> = mutableListOf<MutableList<Int>>()

    imageList = imageToRGBList(img)

    for (x in 0 until width){
        val current = mutableListOf<Int>()
        cutList.add(current)

        for(y in 0 until newHeightValue){
            current.add(imageList[x][y])
        }
    }

    img = rgbListToImage(cutList)

    return img

}
fun removeVerticalSeam(img: BufferedImage, reduceWidthBy: Int): BufferedImage {

    var width = img.width
    val height = img.height
    var img = img
    val newWidthValue = width - reduceWidthBy
    var imageList = imageToRGBList(img)
    while (width != newWidthValue) {
        val energies = countImageEnergy( img)

        val distances = createDistancesArray(energies)
        val shortest = shortestPath(distances)

        imageList = imageToRGBList(img)

        for (y in 0 until height) {

            for (x in shortest[y] until width - 1) {
                imageList[x][y] = imageList[x + 1][y]
            }

        }

        img = rgbListToImage(imageList)

        width --
    }

    img = rgbListToImage(imageList.dropLast(reduceWidthBy))

    return img

}

fun createDistancesArrayHorizonatl(energies: Array<DoubleArray>): Array<DoubleArray> {
    val width = energies.size
    val height = energies[0].size
    val distances: Array<DoubleArray> = Array(width) { DoubleArray(height) { Double.MAX_VALUE } }
    for (y in 0 until height) {

        distances[0][y] = energies[0][y]
    }

    for (x in 1 until width) {
        for(y in 0 until height) {

            val neighbors: Array<Double> = when (y) {
                0 -> {
                    arrayOf(distances[x - 1][y], distances[x - 1][y + 1])
                }
                height - 1 -> {
                    arrayOf(distances[x - 1][y - 1], distances[x - 1][y])
                }
                else -> {
                    arrayOf(distances[x - 1][y - 1], distances[x - 1][y], distances[x - 1][y + 1])
                }
            }

            distances[x][y] = energies[x][y] + neighbors.min()!!
        }
    }
    return distances
}

fun createDistancesArray(energies: Array<DoubleArray>): Array<DoubleArray> {
    val width = energies.size
    val height = energies[0].size
    val distances: Array<DoubleArray> = Array(width) { DoubleArray(height) { Double.MAX_VALUE } }
    for (x in 0 until width) {

        distances[x][0] = energies[x][0]
    }

    for (y in 1 until height) {
        for(x in 0 until width) {

            val neighbors: Array<Double> = when (x) {
                0 -> {
                    arrayOf(distances[x][y - 1], distances[x + 1][y - 1])
                }
                width - 1 -> {
                    arrayOf(distances[x - 1][y - 1], distances[x][y - 1])
                }
                else -> {
                    arrayOf(distances[x - 1][y - 1], distances[x][y - 1], distances[x + 1][y - 1])
                }
            }

            distances[x][y] = energies[x][y] + neighbors.min()!!
        }
    }
    return distances
}

fun shortestPath(distances: Array<DoubleArray>): List<Int> {

    val width = distances.size
    val height = distances[0].size
    val path = mutableListOf<Int>()

    var indexOfMinOfLastRow = -1
    var minOfLastRow = Double.MAX_VALUE
    for (x in 0 until width) {
        if (distances[x][height - 1] < minOfLastRow) {
            indexOfMinOfLastRow = x
            minOfLastRow = distances[x][height - 1]
        }
    }
    path.add(indexOfMinOfLastRow)

    var xIndex = indexOfMinOfLastRow

    for (y in height - 2 downTo 0) {
        var min = Double.MAX_VALUE
        var index = -1

        if (xIndex > 0 && distances[xIndex - 1][y] < min) {
            index = xIndex - 1
            min = distances[index][y]
        }

        if (distances[xIndex][y] < min) {
            index = xIndex
            min = distances[index][y]
        }

        if (xIndex < width - 1 && distances[xIndex + 1][y] < min) {
            index = xIndex + 1
            min = distances[index][y]
        }

        xIndex = index
        path.add(xIndex)
        index = -1
    }

    return path.reversed()

}

fun shortestPathHorizontal(distances: Array<DoubleArray>): List<Int> {
    val width = distances.size
    val height = distances[0].size

    val path = mutableListOf<Int>()

    var indexOfMinOfLastRow = -1
    var minOfLastRow = Double.MAX_VALUE
    for (y in 0 until height){
        if(distances[width - 1][y] < minOfLastRow){
            indexOfMinOfLastRow = y
            minOfLastRow = distances[width - 1][y]
        }
    }
    path.add(indexOfMinOfLastRow)

    var xIndex = indexOfMinOfLastRow

    for (x in width - 2 downTo 0){
        var min = Double.MAX_VALUE
        var index = -1

        if(xIndex > 0 && distances[x][xIndex-1] < min){
            index = xIndex - 1
            min = distances[x][index]
        }

        if(distances[x][xIndex] < min){
            index = xIndex
            min = distances[x][index]
        }

        if(xIndex < height -1 && distances[x][xIndex+1] < min){
            index = xIndex + 1
            min = distances[x][index]
        }

        xIndex = index
        path.add(xIndex)
        index = -1
    }

    return path.reversed()


}

fun countImageEnergy(img: BufferedImage): Array<DoubleArray> {
    val width = img.width
    val height = img.height

    var energies: Array<DoubleArray> = Array(width) { DoubleArray(height) { 0.0 } }

    for (y in 0 until height) {
        for (x in 0 until width) {


            var xp1: Int = when (x) {
                0 -> img.getRGB(x + 2, y)
                width - 1 -> img.getRGB(x, y)
                else -> img.getRGB(x + 1, y)
            }

            var xp2: Int = when (x) {
                width - 1 -> img.getRGB(x - 2, y)
                0 -> img.getRGB(x, y)
                else -> img.getRGB(x - 1, y)
            }

            var yp1: Int = when (y) {
                0 -> img.getRGB(x, y + 2)
                height - 1 -> img.getRGB(x, y)
                else -> img.getRGB(x, y + 1)
            }

            var yp2: Int = when (y) {
                height - 1 -> img.getRGB(x, y - 2)
                0 -> img.getRGB(x, y)
                else -> img.getRGB(x, y - 1)
            }


            val xGradient = getGradient(xp1, xp2)
            val yGradient = getGradient(yp1, yp2)

            val energy = Math.sqrt(xGradient.toDouble() + yGradient.toDouble())
            energies[x][y] = energy
        }
    }
    return energies
}

fun getGradient(p1: Int, p2: Int): Int {

    var xr1 = p1 shr 16 and 0xff
    var xg1 = p1 shr 8 and 0xff
    var xb1 = p1 and 0xff

    var xr2 = p2 shr 16 and 0xff
    var xg2 = p2 shr 8 and 0xff
    var xb2 = p2 and 0xff

    val xRDifference = xr2 - xr1
    val xGDifference = xg2 - xg1
    val xBDifference = xb2 - xb1

    val gradient = xRDifference * xRDifference + xGDifference * xGDifference + xBDifference * xBDifference

    return gradient
}

fun imageToRGBList(img: BufferedImage): MutableList<MutableList<Int>> {

    val list = mutableListOf<MutableList<Int>>()

    for (x in 0 until img.width){
        val current = mutableListOf<Int>()
        list.add(current)

        for(y in 0 until img.height){
            current.add(img.getRGB(x,y))
        }
    }

    return list
}

fun rgbListToImage(rgbList: List<List<Int>>): BufferedImage {

    val img = BufferedImage(rgbList.size, rgbList[0].size, TYPE_INT_RGB)

    for (x in 0 until img.width){
        for(y in 0 until img.height){
            img.setRGB(x, y, rgbList[x][y])
        }
    }

    return img
}


