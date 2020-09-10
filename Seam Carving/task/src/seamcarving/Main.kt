package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {
    //println(args.joinToString())
    var inputImage = args[1]
    val outputImage = args[3]

    val file = File(inputImage)
    //println(file.exists())
    //inputImage = "test/" + inputImage
    val img = ImageIO.read(File(inputImage))

    // Get image width and height
    val width: Int = img.width
    val height: Int = img.height

    val energies = countImageEnergy(width, height, img)
    //setIntensity(img, energies, height, width )
    //findSeam(energies, height, width, img)
    val distances = createDistancesArray(energies, height, width)
    val shortest = shortestPath(distances, width, height, energies)
    drawPath(shortest.toTypedArray(), img, height, width)

    //val dirs = inputImage.replaceAfterLast("/", "")
    //File(dirs).mkdirs()

    ImageIO.write(img, "png", File(outputImage))


}
fun createDistancesArray(energies: Array<DoubleArray>, height: Int, width: Int): Array<DoubleArray> {
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
fun setDistances(energies: Array<DoubleArray>, height: Int, width: Int): Array<DoubleArray> {
    val distances: Array<DoubleArray> = Array(width) { DoubleArray(height) { Double.MAX_VALUE } }
    var firstMinEnergy = getMaxEnergy(energies, height, width)
    var startPosition: Int = 0

    for (x in 0 until width) {

        distances[x][0] = energies[x][0]
        /*if(energies[x][y] < firstMinEnergy) {
            firstMinEnergy = energies[x][y]
            startPosition = x
        }*/
    }


    var currentPosition = startPosition
    var distance = energies[startPosition][0]

    for (x in 0 until width) {
        currentPosition = x
        for (y in 1 until height) {
            var one = 0.0
            var three = 0.0
            val two = energies[currentPosition][y] + distance

            if (currentPosition != 0) {
                one = energies[currentPosition - 1][y] + distance

                if (distances[currentPosition - 1][y] > one) {
                    distances[currentPosition - 1][y] = one
                }

            } else {
                one = energies[currentPosition][y] + distance

                if (distances[currentPosition][y] > one) {
                    distances[currentPosition][y] = one
                }
            }

            if (currentPosition != width - 1) {
                three = energies[currentPosition + 1][y] + distance

                if (distances[currentPosition + 1][y] > three) {
                    distances[currentPosition + 1][y] = three
                }

            } else {
                three = energies[currentPosition][y] + distance

                if (energies[currentPosition][y] > three) {
                    distances[currentPosition][y] = three
                }
            }

            if (distances[currentPosition][y] > two) {
                distances[currentPosition][y] = two
            }


            if (one < two) {
                currentPosition -= 1
                distance = one
            } else if (three < two) {
                currentPosition += 1
                distance = three
            } else {
                distance = two
            }
        }
    }
    return distances
}

fun shortestPath(distances: Array<DoubleArray>, width: Int, height: Int, energies: Array<DoubleArray>): List<Int> {

    val path = mutableListOf<Int>()

    var indexOfMinOfLastRow = -1
    var minOfLastRow = Double.MAX_VALUE
    for (x in 0 until width){
        if(distances[x][height-1] < minOfLastRow){
            indexOfMinOfLastRow = x
            minOfLastRow = distances[x][height-1]
        }
    }
    path.add(indexOfMinOfLastRow)

    var xIndex = indexOfMinOfLastRow

    for (y in height - 2 downTo 0){
        var min = Double.MAX_VALUE
        var index = -1

        if(xIndex > 0 && distances[xIndex-1][y] < min){
            index = xIndex - 1
            min = distances[index][y]
        }

        if(distances[xIndex][y] < min){
            index = xIndex
            min = distances[index][y]
        }

        if(xIndex < width -1 && distances[xIndex+1][y] < min){
            index = xIndex + 1
            min = distances[index][y]
        }

        xIndex = index
        path.add(xIndex)
        index = -1
    }

    return path.reversed()


    /*val shortest: Array<Int> = Array(height) { 0 }

    var minDistance = distances[0][height - 1]
    var minDisX = 0
    for (x in 1 until width) {
        if (distances[x][height - 1] < minDistance) {
            minDistance = distances[x][height - 1]
            minDisX = x
        }
    }
    var currentPosition = minDisX
    for (y in height - 1 downTo 0) {
        var one = 0.0
        var three = 0.0
        val two = distances[currentPosition][y]

        if (currentPosition != 0) {
            one = distances[currentPosition - 1][y]
        } else {
            one = distances[currentPosition][y]

        }

        if (currentPosition != width - 1) {
            three = distances[currentPosition + 1][y]

        } else {
            three = distances[currentPosition][y]

        }


        if (one < two) {
            currentPosition -= 1
        } else if (three < two) {
            currentPosition += 1
        }

        shortest[y] = currentPosition
    }
    *//* var minX = 0
     val minVal = getMaxEnergy(energies, height, width)
     for (x in 0 until width ) {
         if (distances[x][y] < minVal && distances[x][y] != 0.0) {
             minX = x
         }
     }
     shortest[y] = minX*//*

    return shortest*/
}

fun drawPath(shortest: Array<Int>, img: BufferedImage, height: Int, width: Int) {
    for (y in 0 until height) {
        changeColor(img, shortest[y], y, 255, 0, 0)
    }

}

fun findSeam(energies: Array<DoubleArray>, height: Int, width: Int, img: BufferedImage) {
    var firstMinEnergy = getMaxEnergy(energies, height, width)
    var Xposition = 0
    var distances: MutableMap<Int, Double> = mutableMapOf()
    var distance = 0.0


    for (x in 0 until width) {
        /*val y = 0
        if(energies[x][y] < firstMinEnergy) {
            firstMinEnergy = energies[x][y]
            Xposition = x
        }*/

        //changeColor(img, Xposition, 0, 255, 0, 0)
        for (y in 1 until height) {
            Xposition = x

            if (Xposition == 0) {
                if (energies[Xposition][y] < energies[Xposition + 1][y]) {
                    firstMinEnergy = energies[Xposition][y]
                } else {
                    firstMinEnergy = energies[Xposition + 1][y]
                    Xposition += 1
                }
            } else if (Xposition == width - 1) {
                if (energies[Xposition - 1][y] < energies[Xposition][y]) {
                    firstMinEnergy = energies[Xposition - 1][y]
                    Xposition -= 1
                } else {
                    firstMinEnergy = energies[Xposition][y]
                }
            } else {
                if (energies[Xposition - 1][y] < energies[Xposition][y]) {
                    firstMinEnergy = energies[Xposition - 1][y]
                    Xposition -= 1
                } else {
                    firstMinEnergy = energies[Xposition][y]
                }

                if (energies[Xposition + 1][y] < firstMinEnergy) {
                    firstMinEnergy = energies[Xposition + 1][y]
                    Xposition += 1
                }
            }
            distance += firstMinEnergy
            distances.put(x, distance)
            //changeColor(img, Xposition, y, 255, 0, 0)

        }
    }
    var xMin = 0
    var minDistance = distances[0]!!
    for (x in 1 until width) {
        if (distances[x]!! < minDistance) {
            minDistance = distances[x]!!
            xMin = x
        }
    }

    val z = 0
    firstMinEnergy = energies[xMin][z]
    Xposition = xMin

    changeColor(img, Xposition, 0, 255, 0, 0)
    for (y in 1 until height) {

        if (Xposition == 0) {
            if (energies[Xposition][y] < energies[Xposition + 1][y]) {
                firstMinEnergy = energies[Xposition][y]
            } else {
                firstMinEnergy = energies[Xposition + 1][y]
                Xposition += 1
            }
        } else if (Xposition == width - 1) {
            if (energies[Xposition - 1][y] < energies[Xposition][y]) {
                firstMinEnergy = energies[Xposition - 1][y]
                Xposition -= 1
            } else {
                firstMinEnergy = energies[Xposition][y]
            }
        } else {
            if (energies[Xposition - 1][y] < energies[Xposition][y]) {
                firstMinEnergy = energies[Xposition - 1][y]
                Xposition -= 1
            } else {
                firstMinEnergy = energies[Xposition][y]
            }

            if (energies[Xposition + 1][y] < firstMinEnergy) {
                firstMinEnergy = energies[Xposition + 1][y]
                Xposition += 1
            }
        }

        changeColor(img, Xposition, y, 255, 0, 0)

    }


}

fun changeColor(img: BufferedImage, x: Int, y: Int, rValue: Int, gValue: Int, bValue: Int) {
    val pixel = img.getRGB(x, y)

    //Creating a Color object from pixel value
    var color = Color(pixel, true)

    //Retrieving the R G B values
    var red: Int = color.getRed()
    var green: Int = color.getGreen()
    var blue: Int = color.getBlue()

    //Modifying the RGB values
    green = gValue
    blue = bValue
    red = rValue

    //Creating new Color object
    color = Color(red, green, blue)

    //Setting new Color object to the image
    img.setRGB(x, y, color.rgb)
}

fun setIntensity(img: BufferedImage, energies: Array<DoubleArray>, height: Int, width: Int) {

    val maxEnergy = getMaxEnergy(energies, height, width)
    //println(maxEnergy)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val intensity = (255.0 * energies[x][y] / maxEnergy).toInt()

            val pixel = img.getRGB(x, y)

            //Creating a Color object from pixel value
            var color = Color(pixel, true)

            //Retrieving the R G B values
            var red: Int = color.getRed()
            var green: Int = color.getGreen()
            var blue: Int = color.getBlue()

            //Modifying the RGB values
            green = intensity
            blue = intensity
            red = intensity

            //Creating new Color object
            color = Color(red, green, blue)

            //Setting new Color object to the image
            img.setRGB(x, y, color.rgb)

        }
    }
}

fun getMaxEnergy(energies: Array<DoubleArray>, height: Int, width: Int): Double {
    var maxEnergy = 0.0
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (energies[x][y] > maxEnergy) {
                maxEnergy = energies[x][y]
            }
        }
    }
    return maxEnergy
}

fun countImageEnergy(width: Int, height: Int, img: BufferedImage): Array<DoubleArray> {
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


