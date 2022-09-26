package com.cursosandroidant.fundamentoscorrutinas

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/****
 * Project: Fundamentos Corrutinas
 * From: com.cursosandroidant.fundamentoscorrutinas
 * Created by Alain Nicolás Tello on 04/09/21 at 13:19
 * All rights reserved 2021.
 *
 * All my Udemy Courses:
 * https://www.udemy.com/user/alain-nicolas-tello/
 * Web: www.alainnicolastello.com
 ***/
fun main() {
    //coldFlow()
    //cancelFlow()
    //flowOperators()
    //terminalFlowOperators()
    bufferFlow()
    //conflationFlow()
    //multiFlow()
    //flatFlows()
    //flowExceptions()
    completions()
}

fun completions() {
    runBlocking {
        newTopic("Fin de un Flujo(onCompletion)")
        getCitiesFlow()
            .onCompletion { println("Quitar el progressBar...") }
            //.collect { println(it) }
        println()

        getMatchResultsFlow()
            .onCompletion { println("Mostrar las estadísticas...") }
            .catch { emit("Error: $this") }
            .collect { println(it) }

        newTopic("Cancelar Flow")
        getDataByFlowStatic()
            .onCompletion { println("Ya no le interesa al usuario...") }
            .cancellable()
            .collect {
                if (it > 29.5f) cancel()
                println(it)
            }
    }
}

fun flowExceptions() {
    runBlocking {
        newTopic("Control de errores")
        newTopic("Try/Catch")
        /*try {
            getMatchResultsFlow()
                .collect {
                    println(it)
                    if (it.contains("2")) throw Exception("Habían acordado 1-1 :v")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        newTopic("Transparencia")
        getMatchResultsFlow()
            .catch {
                emit("Error: $this")
            }
            .collect {
                println(it)
                if (!it.contains("-")) println("Notifica al programador...")
            }
    }
}

fun flatFlows() {
    runBlocking {
        newTopic("Flujos de aplanamiento")

        newTopic("FlatMapConcat")
        getCitiesFlow()
            .flatMapConcat { city -> //Flow<Flow<TYPE>>
                getDataToFlatFlow(city)
            }
            .map { setFormat(it) }
            .collect { println(it) }

        newTopic("FlatMapMerge")
        getCitiesFlow()
            .flatMapMerge { city -> //Flow<Flow<TYPE>>
                getDataToFlatFlow(city)
            }
            .map { setFormat(it) }
            .collect { println(it) }
    }
}

fun getDataToFlatFlow(city: String): Flow<Float> = flow {
    (1..3).forEach {
        println("Temperatura de ayer en $city...")
        emit(Random.nextInt(10, 30).toFloat())

        println("Temperatura actual en $city:")
        delay(100)
        emit(20 + it + Random.nextFloat())
    }
}

fun getCitiesFlow(): Flow<String> = flow {
    listOf("Santander", "CDMX", "Lima")
        .forEach { city ->
            println("\nConsultando ciudad...")
            delay(1_000)
            emit(city)
        }
}

fun multiFlow() {
    runBlocking {
        newTopic("Zip $ Combine")
        getDataByFlowStatic()
            .map { setFormat(it) }
            .combine(getMatchResultsFlow()){ degrees, result ->
            //.zip(getMatchResultsFlow()){ degrees, result ->
                "$result with $degrees"
            }
            .collect { println(it) }
    }
}

fun conflationFlow() {
    runBlocking {
        newTopic("Fusión")
        val time = measureTimeMillis {
            getMatchResultsFlow()
                .conflate() //2558ms
                //.buffer() //4820ms
                //.collectLatest {//2597ms
                .collect {  //7087ms
                    delay(100)
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun getMatchResultsFlow(): Flow<String> {
    return flow {
        var homeTeam = 0
        var awayTeam = 0
        (0..45).forEach {
            println("minuto: $it")
            delay(50)
            homeTeam += Random.nextInt(0, 21)/20
            awayTeam += Random.nextInt(0, 21)/20
            emit("$homeTeam-$awayTeam")

            if (homeTeam == 2 || awayTeam == 2) throw Exception("Habían acordado 1 y 1 :v")
        }
    }
}

fun bufferFlow() {
    runBlocking {
        newTopic("Buffer para Flow")
        val time = measureTimeMillis {
            getDataByFlowStatic()
                .map { setFormat(it) }
                .buffer()
                .collect {              //000111222333444
                    delay(500) //   0000011111222223333344444
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}
fun getDataByFlowStatic(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("procesando datos...")
            delay(300)
            emit(20 + it + Random.nextFloat())
        }
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("Operadores Flow Terminales")
        newTopic("List")
        val list = getDataByFlow()
            //.toList()
        println("List: $list")

        newTopic("Single")
        val single = getDataByFlow()
            //.take(1)
            //.single()
        println("Single: $single")

        newTopic("First")
        val first = getDataByFlow()
            //.first()
        println("First: $first")

        newTopic("Last")
        val last = getDataByFlow()
            //.last()
        println("First: $last")

        newTopic("Reduce")
        val saving = getDataByFlow()
            .reduce { accumulator, value ->
                println("Accumulator: $accumulator")
                println("Value: $value")
                println("Current saving: ${accumulator + value}")
                accumulator + value
            }
        println("Saving: $saving")

        newTopic("Fold")
        val lastSaving = saving
        val totalSaving = getDataByFlow()
            .fold(lastSaving, { acc, value ->
                println("Accumulator: $acc")
                println("Value: $value")
                println("Current saving: ${acc + value}")
                acc + value
            })
        println("TotalSaving: $totalSaving")
    }
}

fun flowOperators() {
    runBlocking {
        newTopic("Operadores Flow Intermediarios")
        newTopic("Map")
        getDataByFlow()
            .map {
                //setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
            //.collect { println(it) }

        newTopic("Filter")
        getDataByFlow()
            .filter {
                it > 23
            }
            .map {
                setFormat(it)
            }
            //.collect { println(it) }

        newTopic("Transform")
        getDataByFlow()
            .transform {
                emit(setFormat(it))
                emit(setFormat(convertCelsToFahr(it), "F"))
            }
            //.collect { println(it) }

        newTopic("Take")
        getDataByFlow()
            .take(3)
            .map { setFormat(it) }
            .collect { println(it) }
    }
}

fun convertCelsToFahr(cels: Float): Float = ( (cels * 9) / 5 ) + 32

fun setFormat(temp: Float, degree: String = "C"): String = String.format(Locale.getDefault(),
    "%.1fº$degree", temp)

fun cancelFlow() {
    runBlocking {
        newTopic("Cancelar flow")
        val job = launch {
            getDataByFlow().collect { println(it) }
        }
        delay(someTime()*2)
        job.cancel()
    }
}

fun coldFlow() {
    newTopic("Flows are Cold")
    runBlocking {
        val dataFlow = getDataByFlow()
        println("esperando...")
        delay(someTime())
        dataFlow.collect { println(it) }
    }
}
