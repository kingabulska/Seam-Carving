fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Invalid number of program arguments")
    } else {
        val arg1 = args[0].length
        val arg2 = args[1].length
        val arg3 = args[2].length

        println("Argument 1: ${args[0]}. It has $arg1 characters")
        println("Argument 2: ${args[1]}. It has $arg2 characters")
        println("Argument 3: ${args[2]}. It has $arg3 characters")
    }
}
