package com.codexo.minidraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_undo.setOnClickListener {
            draw_board.onUndo()
        }

        btn_redo.setOnClickListener {
            draw_board.onRedo()
        }

        btn_clear.setOnClickListener {
            draw_board.onClearDrawing()
        }
    }
}