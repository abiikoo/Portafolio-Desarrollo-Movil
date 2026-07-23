fun main() {

    var nombre = ""
    var apellido = ""
    var edad = 0
    var genero = ""

    val precioBase = 20.0
    var opcion: Int

    do {
        println("\n--- MENU ---")
        println("1. Registrar pasajero")
        println("2. Comprar boleto")
        println("3. Salir")
        print("Seleccione una opción: ")
        opcion = readLine()!!.toInt()

        when (opcion) {

            1 -> {
                print("Ingrese nombre: ")
                nombre = readLine()!!

                print("Ingrese apellido: ")
                apellido = readLine()!!

                print("Ingrese edad: ")
                edad = readLine()!!.toInt()

                print("Ingrese género (M/F): ")
                genero = readLine()!!.uppercase()
            }

            2 -> {
                if (nombre.isEmpty() || apellido.isEmpty()) {
                    println("⚠️ Primero debe registrar un pasajero.")
                } else {

                    val nombreCompleto = "$nombre $apellido"
                    var descuento = 0.0

                    // Descuentos
                    if (edad < 12) {
                        descuento = 0.05
                    } else if ((genero == "F" && edad > 57) || (genero == "M" && edad > 62)) {
                        descuento = 0.15
                    }

                    val total = precioBase - (precioBase * descuento)

                    print("Ingrese tipo de pago: ")
                    val tipoPago = readLine()!!

                    // Recibo
                    println("\n--- TRANSPORTE UTP S.A. -----")
                    println("RUC: 01-2531-4507")
                    println("\nTERMINAL PRINCIPAL\n")

                    println("CLIENTE: $nombreCompleto")
                    println("EDAD: $edad")
                    println("GENERO: $genero")
                    println("PAGO: $tipoPago")
                    println("COSTO: B/ %.2f".format(total))

                    println("\nTENGA UN EXCELENTE VIAJE!")
                }
            }

            3 -> println("Saliendo del sistema...")

            else -> println("Opción inválida")
        }

    } while (opcion != 3)
}
