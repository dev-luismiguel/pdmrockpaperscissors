package com.example.luismiguel.pdmrockpaperscissors

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit private var imm: InputMethodManager
    lateinit private var inputPlayerOneName: EditText
    lateinit private var inputPlayerTwoName: EditText

    private val ROCK_ID = 0
    private val PAPER_ID = 1
    private val SCISSORS_ID = 2

    private var playerPlaying = 0

    private var playerOneName = ""
    private var playerTwoName = ""

    private var playerOneChoice: Int? = null
    private var playerTwoChoice: Int? = null

    private var winsPlayerOne = 0
    private var winsPlayerTwo = 0

    private var singlePlayer = true

    lateinit private var handler : SQLiteHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputPlayerOneName = EditText(this)
        inputPlayerTwoName = EditText(this)

        imageChoiceRock.setOnClickListener(this)
        imageChoicePaper.setOnClickListener(this)
        imageChoiceScissors.setOnClickListener(this)

        handler = SQLiteHandler(this@GameActivity)

        singlePlayer = intent.getBooleanExtra("singlePlayer", true)

        if (singlePlayer){
            playerTwoName = "CPU"
            labelPlayerTwoNameScore.text = "CPU"
        }

        val playerOneAlert = AlertDialog.Builder(this@GameActivity).create()
        playerOneAlert.setTitle("Player #1 Name")
        playerOneAlert.setView(inputPlayerOneName)

        playerOneAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", {
            dialogInterface, i -> finish()
        })

        playerOneAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
            dialogInterface, i -> setPlayerOneName()
        })

        playerOneAlert.show()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onClick(view: View){
        when (view?.id){
            R.id.imageChoiceRock -> chooseRock()
            R.id.imageChoicePaper -> choosePaper()
            R.id.imageChoiceScissors -> chooseScissors()
        }
    }

    override fun finish(){
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        super.finish()
    }

    private fun setPlayerOneName(){
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        playerOneName = inputPlayerOneName.text.toString()
        labelPlayerOneNameScore.text = playerOneName
        labelWhoPlaying.text = playerOneName

        if (!singlePlayer){
            val playerTwoAlert = AlertDialog.Builder(this@GameActivity).create()
            playerTwoAlert.setTitle("Player #2 Name")
            playerTwoAlert.setView(inputPlayerTwoName)
            playerTwoAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", {
                dialogInterface, i -> finish()
            })

            playerTwoAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
                dialogInterface, i -> setPlayerTwoName()
            })

            playerTwoAlert.show()
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        else {
            checkIfHaveMatch()
        }
    }

    private fun setPlayerTwoName(){
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        playerTwoName = inputPlayerTwoName.text.toString()
        labelPlayerTwoNameScore.text = playerTwoName
        checkIfHaveMatch()
    }

    private fun chooseRock() {
//        Toast.makeText(this, "ROCK BABY!", Toast.LENGTH_SHORT).show()
        choose(ROCK_ID)
    }

    private fun choosePaper(){
        choose(PAPER_ID)
    }

    private fun chooseScissors(){
        choose(SCISSORS_ID)
    }

    private fun choose(choiceId: Int) {
        if (singlePlayer)
            playerTwoChoice = Random().nextInt(3)

        if (playerOneChoice == null){
            playerOneChoice = choiceId
            changeWhoPlaying()

            imagePlayerOneSelected.setImageDrawable(getImageById(3))
            imagePlayerTwoSelected.setImageDrawable(getImageById(3))
            imageWinner.setImageDrawable(getImageById(3))
        }
        else if (playerTwoChoice == null)
            playerTwoChoice = choiceId

        if (playerOneChoice != null && playerTwoChoice != null)
        {
            imagePlayerOneSelected.setImageDrawable(getImageById(playerOneChoice?:0))
            imagePlayerTwoSelected.setImageDrawable(getImageById(playerTwoChoice?:0))

            getMatch()

            playerOneChoice = null
            playerTwoChoice = null
            changeWhoPlaying()
        }
    }

    private fun changeWhoPlaying(){
        if (playerPlaying == 0) {
            playerPlaying = 1
            labelWhoPlaying.text = playerTwoName
        }
        else {
            playerPlaying = 0
            labelWhoPlaying.text = playerOneName
        }
    }

    private fun checkIfHaveMatch(){
        var match = handler.getMatch(playerOneName, playerTwoName)
        if (match != null){

            val resumeMatchAlert = AlertDialog.Builder(this@GameActivity).create()
            resumeMatchAlert.setTitle("Retomar")
            resumeMatchAlert.setMessage("Deseja retomar o plancar anterior entre esses dois players ou iniciar uma nova rodada?")
            resumeMatchAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Nova", {
                dialogInterface, i -> deleteMatch()
            })

            resumeMatchAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Retomar", {
                dialogInterface, i -> resumeMatch(match)
            })

            resumeMatchAlert.show()
        }
        else{
            insertMatch()
        }
    }

    private fun deleteMatch() {
        handler.deleteMatch(playerOneName, playerTwoName)
        insertMatch()
    }

    private fun resumeMatch(match: Match){
        winsPlayerOne = match.Player1Wins
        labelPlayerOneScore.text = winsPlayerOne.toString()
        winsPlayerTwo = match.Player2Wins
        labelPlayerTwoScore.text = winsPlayerTwo.toString()
    }

    private fun insertMatch(){
        handler.insertMatch(playerOneName, 0, playerTwoName, 0)
    }

    private fun getImageById(id: Int): Drawable {
        return when (id){
            0 -> resources.getDrawable(R.drawable.pedra)
            1 -> resources.getDrawable(R.drawable.papel)
            2 -> resources.getDrawable(R.drawable.tesoura)
            else -> resources.getDrawable(R.drawable.padrao)
        }
    }

    private fun getMatch(){
        // Player #1 winner
        if ((playerOneChoice == ROCK_ID     && playerTwoChoice == SCISSORS_ID) ||
            (playerOneChoice == PAPER_ID    && playerTwoChoice == ROCK_ID)     ||
            (playerOneChoice == SCISSORS_ID && playerTwoChoice == PAPER_ID)){

            imageWinner.setImageDrawable(getImageById(playerOneChoice?:0))
            winsPlayerOne++
            labelPlayerOneScore.text = winsPlayerOne.toString()

        }
        // Player #2 winner
        else if ((playerTwoChoice == ROCK_ID     && playerOneChoice == SCISSORS_ID) ||
                 (playerTwoChoice == PAPER_ID    && playerOneChoice == ROCK_ID)     ||
                 (playerTwoChoice == SCISSORS_ID && playerOneChoice == PAPER_ID)){

            imageWinner.setImageDrawable(getImageById(playerTwoChoice?:0))
            winsPlayerTwo++
            labelPlayerTwoScore.text = winsPlayerTwo.toString()

        }
        // Draw
        else {

            imageWinner.setImageDrawable(getImageById(3))

        }

        handler.updateMatch(playerOneName, playerTwoName, winsPlayerOne, winsPlayerTwo)
    }

}
