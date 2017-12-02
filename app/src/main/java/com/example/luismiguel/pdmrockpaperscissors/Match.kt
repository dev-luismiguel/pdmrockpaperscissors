package com.example.luismiguel.pdmrockpaperscissors

/**
 * Created by Luis Miguel on 02/12/2017.
 */
data class Match(val Id: Long, val Player1Name: String, val Player1Wins: Int, val Player2Name: String, val Player2Wins: Int)  {
    constructor(Player1Name: String, Player1Wins: Int, Player2Name: String, Player2Wins: Int) : this(0, Player1Name, Player1Wins, Player2Name, Player2Wins)
}