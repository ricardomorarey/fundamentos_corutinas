package com.cursosandroidant.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/****
 * Project: Fundamentos Corrutinas
 * From: com.cursosandroidant.fundamentoscorrutinas
 * Created by Alain Nicolás Tello on 03/09/21 at 13:47
 * All rights reserved 2021.
 *
 * All my Udemy Courses:
 * https://www.udemy.com/user/alain-nicolas-tello/
 * Web: www.alainnicolastello.com
 ***/
fun main(){
    //dispatchers()
    //nested()
    //changeWithContext()
    basicFlows()
}

fun basicFlows() {
    newTopic("Flows básicos")
    runBlocking {
        launch { getDataByFlow().collect { println(it) } }

        launch {
            (1..50).forEach {
                delay(someTime()/10)
                println("Tarea 2...")
            }
        }
    }
}
fun getDataByFlow(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("procesando datos...")
            delay(someTime())
            emit(20 + it + Random.nextFloat())
        }
    }
}

fun changeWithContext() {
    runBlocking {
        newTopic("withContext")
        startMsg()

        withContext(newSingleThreadContext("Cursos Android ANT")){
            startMsg()
            delay(someTime())
            println("CursosAndroidANT")
            endMsg()
        }

        withContext(Dispatchers.IO){
            startMsg()
            delay(someTime())
            println("Petición al servidor")
            endMsg()
        }

        endMsg()
    }
}

fun nested() {
    runBlocking {
        newTopic("Anidar")

        val job = launch {
            startMsg()

            launch {
                startMsg()
                delay(someTime())
                println("Otra tarea")
                endMsg()
            }

            val subJob = launch(Dispatchers.IO) {
                startMsg()

                launch(newSingleThreadContext("Cursos Android ANT")) {
                    startMsg()
                    println("tarea cursos android ant")
                    endMsg()
                }

                delay(someTime())
                println("tarea en el servidor")
                endMsg()
            }
            delay(someTime()/4)
            subJob.cancel()
            println("SubJob cancelado...")

            var sum = 0
            (1..100).forEach {
                sum += it
                delay(someTime()/100)
            }
            println("Sum = $sum")
            endMsg()
        }

        delay(someTime()/2)
        job.cancel()
        println("Job cancelado...")
    }
}

fun dispatchers() {
    runBlocking {
        newTopic("Dispatchers")
        launch {
            startMsg()
            println("None")
            endMsg()
        }
        launch(Dispatchers.IO) {
            startMsg()
            println("IO")
            endMsg()
        }
        launch(Dispatchers.Unconfined) {
            startMsg()
            println("Unconfined")
            endMsg()
        }
        //Main solo para android
        launch(Dispatchers.Default) {
            startMsg()
            println("Default")
            endMsg()
        }
        launch(newSingleThreadContext("Cursos Android ANT")) {
            startMsg()
            println("Mi corrutina personalizada con un dispatcher")
            endMsg()
        }
        newSingleThreadContext("CursosAndroidANT").use { myContext ->
            launch(myContext) {
                startMsg()
                println("Mi corrutina personalizada con un dispatcher2")
                endMsg()
            }
        }
    }
}
